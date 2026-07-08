package com.samtar.userservice.dto.common;

import com.samtar.enums.ROLE;

import javax.management.relation.Role;

public record JwtClaimsDto(
        String username,
        ROLE userRole
) {

}
