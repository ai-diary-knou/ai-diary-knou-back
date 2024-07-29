package com.aidiary.gateway.presentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HeathController {

    @GetMapping("/")
    public ResponseEntity healthCheck(){
        log.info("Health Check");
        return ResponseEntity.ok("success!");
    }

}
