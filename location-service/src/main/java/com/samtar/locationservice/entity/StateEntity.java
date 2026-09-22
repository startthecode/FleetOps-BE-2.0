package com.samtar.locationservice.entity;

import com.samtar.locationservice.constants.MessageConstant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "states",
        indexes = {
                @Index(name = "idx_state_country_id", columnList = "country_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_state_code", columnNames = {"country_id", "code"})
        }
)
public class StateEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_state_country"))
    @NotNull(message = MessageConstant.COUNTRY_ID_MANDATORY)
    private CountryEntity country;

    @Column(nullable = false, length = 100)
    @NotBlank(message = MessageConstant.STATE_NAME_MANDATORY)
    private String name;

    @Column(nullable = false, length = 10)
    @NotBlank(message = MessageConstant.STATE_CODE_MANDATORY)
    private String code;

    // Inverse side: cities own the state_id column. No cascade - deleting a state
    // that still has cities is rejected in StateService.
    @OneToMany(mappedBy = "state", fetch = FetchType.LAZY)
    private List<CityEntity> cities = new ArrayList<>();
}
