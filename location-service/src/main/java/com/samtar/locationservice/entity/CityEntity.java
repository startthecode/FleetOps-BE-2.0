package com.samtar.locationservice.entity;

import com.samtar.locationservice.constants.MessageConstant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cities",
        indexes = {
                @Index(name = "idx_city_state_id", columnList = "state_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_city_name", columnNames = {"state_id", "name"})
        }
)
public class CityEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_city_state"))
    @NotNull(message = MessageConstant.STATE_ID_MANDATORY)
    private StateEntity state;

    @Column(nullable = false, length = 100)
    @NotBlank(message = MessageConstant.CITY_NAME_MANDATORY)
    private String name;
}
