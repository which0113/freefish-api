package com.which.apiinterface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author which
 */
@RestController
@RequestMapping("/")
public class ServiceController {

    @GetMapping("/name")
    public String getName(String name) {
        return "GET " + name;
    }

}
