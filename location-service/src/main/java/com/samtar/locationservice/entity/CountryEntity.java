package com.samtar.locationservice.entity;

import com.samtar.locationservice.constants.MessageConstant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Table(name = "countries",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_country_name", columnNames = {"name"}),
                @UniqueConstraint(name = "uk_country_iso2", columnNames = {"iso2"}),
                @UniqueConstraint(name = "uk_country_iso3", columnNames = {"iso3"})
        }
)
public class CountryEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    @NotBlank(message = MessageConstant.COUNTRY_NAME_MANDATORY)
    private String name;

    @Column(nullable = false, length = 2)
    @NotBlank(message = MessageConstant.COUNTRY_ISO2_MANDATORY)
    @Size(min = 2, max = 2, message = MessageConstant.COUNTRY_ISO2_LENGTH)
    private String iso2;

    @Column(nullable = false, length = 3)
    @NotBlank(message = MessageConstant.COUNTRY_ISO3_MANDATORY)
    @Size(min = 3, max = 3, message = MessageConstant.COUNTRY_ISO3_LENGTH)
    private String iso3;

    @Column(name = "phone_code", length = 10)
    @Size(max = 10, message = MessageConstant.COUNTRY_PHONE_CODE_MAX_LENGTH)
    private String phoneCode;

    // Inverse side: states own the country_id column. No cascade - deleting a country
    // that still has states is rejected in CountryService.
    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<StateEntity> states = new ArrayList<>();
}
