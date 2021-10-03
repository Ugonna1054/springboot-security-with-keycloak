package org.ugonna.springboot.keycloak.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import org.ugonna.springboot.keycloak.dto.LoginRequest;
import org.ugonna.springboot.keycloak.dto.LoginResponse;
import org.ugonna.springboot.keycloak.service.LoginService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class LoginController {

    Logger log = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    LoginService loginService;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login (HttpServletRequest request,
                                                                       @RequestBody LoginRequest loginRequest) throws Exception {
        log.info("Executing login");

        ResponseEntity<LoginResponse> response = null;
        response = loginService.login(loginRequest);

        return response;
    }


}