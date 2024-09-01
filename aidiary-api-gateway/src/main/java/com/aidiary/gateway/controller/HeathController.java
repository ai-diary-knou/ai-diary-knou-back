package com.aidiary.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HeathController {

    @RequestMapping("/")
    public ResponseEntity getHealthCheck(){
        log.info("Health Check");
        return ResponseEntity.ok("success!");
    }


}
