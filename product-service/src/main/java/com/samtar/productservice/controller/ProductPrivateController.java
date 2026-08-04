package com.samtar.productservice.controller;


import com.samtar.enums.ROLE;
import com.samtar.productservice.annotation.AuthorityAnnotation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product/private")
public class ProductPrivateController {
    String[] permittedRoles = {ROLE.SUPER_ADMIN.toString(), ROLE.ADMIN.toString(),ROLE.VENDOR.toString()};
    @AuthorityAnnotation
    public String createProduct(){
        return "";
    }
}
