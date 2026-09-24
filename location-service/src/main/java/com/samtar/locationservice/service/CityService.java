package com.samtar.locationservice.service;

import com.samtar.avro.CityCreatedEvent;
import com.samtar.consts.KafkaTopics;
import com.samtar.enums.OutboxStatus;
import com.samtar.exception.BaseException;
import com.samtar.locationservice.constants.MessageConstant;
import com.samtar.locationservice.dto.request.CreateCityReqDto;
import com.samtar.locationservice.dto.request.UpdateCityReqDto;
import com.samtar.locationservice.dto.response.CityRespDto;
import com.samtar.locationservice.entity.CityEntity;
import com.samtar.locationservice.entity.OutboxEventEntity;
import com.samtar.locationservice.entity.StateEntity;
import com.samtar.locationservice.mapper.CityMapper;
import com.samtar.locationservice.repository.CityRepository;
import com.samtar.locationservice.repository.OutboxEventRepository;
import com.samtar.locationservice.repository.StateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CityService {
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final CityMapper cityMapper;
    private final OutboxEventRepository outboxEventRepository;


    @Transactional
    public CityRespDto create(CreateCityReqDto payload) {
        StateEntity state = requireState(UUID.fromString(payload.stateId()));
        if (cityRepository.existsByState_IdAndNameIgnoreCase(state.getId(), payload.name().trim())) {
            throw new BaseException(MessageConstant.CITY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        CityEntity city = cityMapper.toEntity(payload);
        city.setState(state);
        city.setName(payload.name().trim());
        CityEntity response = cityRepository.save(city);
        creationEvent(response);
        return cityMapper.toResponse(response);
    }

    @Transactional
    public CityRespDto update(UpdateCityReqDto payload) {
        CityEntity city = findEntity(payload.cityId());
        UUID currentStateId = city.getState().getId();

        StateEntity targetState = null;
        if (payload.stateId() != null) {
            UUID requestedStateId = UUID.fromString(payload.stateId());
            if (!requestedStateId.equals(currentStateId)) {
                targetState = requireState(requestedStateId);
            }
        }

        // uk_city_name is (state_id, name), so a change to either side needs the re-check.
        UUID targetStateId = targetState != null ? targetState.getId() : currentStateId;
        String targetName = payload.name() != null ? payload.name().trim() : city.getName();
        boolean keyChanged = !targetStateId.equals(currentStateId)
                || !targetName.equalsIgnoreCase(city.getName());
        if (keyChanged && cityRepository.existsByState_IdAndNameIgnoreCase(targetStateId, targetName)) {
            throw new BaseException(MessageConstant.CITY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        cityMapper.toUpdatedEntity(city, payload);
        if (targetState != null) city.setState(targetState);
        if (payload.name() != null) city.setName(payload.name().trim());
        return cityMapper.toResponse(cityRepository.save(city));
    }

    @Transactional
    public void delete(String cityId) {
        CityEntity city = findEntity(cityId);
        cityRepository.delete(city);
        deletionEvent(city);
    }

    @Transactional
    public CityRespDto findById(String cityId) {
        return cityMapper.toResponse(findEntity(cityId));
    }

    @Transactional
    public List<CityRespDto> findAll() {
        return cityMapper.toResponse(cityRepository.findAll());
    }

    @Transactional
    public List<CityRespDto> findByState(String stateId) {
        UUID id = UUID.fromString(stateId);
        requireState(id);
        return cityMapper.toResponse(cityRepository.findByState_Id(id));
    }

    private StateEntity requireState(UUID stateId) {
        return stateRepository.findById(stateId)
                .orElseThrow(() -> new BaseException(MessageConstant.STATE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private CityEntity findEntity(String cityId) {
        return cityRepository.findById(UUID.fromString(cityId))
                .orElseThrow(() -> new BaseException(MessageConstant.CITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }


    private void creationEvent(CityEntity city) {
        CityCreatedEvent cityCreatedEvent = CityCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setCityId(city.getId().toString())
                .build();
        outBoxInsertion(city, cityCreatedEvent, KafkaTopics.CITY_CREATED);
    }

    private void deletionEvent(CityEntity city) {
        CityCreatedEvent cityCreatedEvent = CityCreatedEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setCityId(city.getId().toString())
                .build();
        outBoxInsertion(city, cityCreatedEvent, KafkaTopics.CITY_DELETED);
    }

    @Transactional
    private void outBoxInsertion(CityEntity cityEntity, SpecificRecordBase payload, String eventTopic) {
        try {
            OutboxEventEntity evntEntity = new OutboxEventEntity();
            evntEntity.setAggregateId(cityEntity.getId());
            evntEntity.setPayload(encodeAvro(payload));
            evntEntity.setTopic(eventTopic);
            evntEntity.setRetryCount(0);
            evntEntity.setStatus(OutboxStatus.PENDING);
            evntEntity.setCreatedAt(Instant.now());
            outboxEventRepository.save(evntEntity);
        } catch (Exception e) {
            throw new BaseException(MessageConstant.FAIL_TO_EXECUTE, HttpStatus.CONFLICT);
        }
    }

    private static String encodeAvro(SpecificRecordBase record) throws IOException {
        SpecificDatumWriter<SpecificRecordBase> writer = new SpecificDatumWriter<>(record.getSchema());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(record, encoder);
        encoder.flush();
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }
}
