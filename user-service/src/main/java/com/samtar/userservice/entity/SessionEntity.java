package com.samtar.userservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "session_table", uniqueConstraints = {
@UniqueConstraint(columnNames = { "refresh_token", "user_id" })
})
public class SessionEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UsersEntity user;

    @Column(name = "ip_address", nullable = false)
    @NotBlank(message = "IP address cannot be blank")
    private String ipAddress;
}
