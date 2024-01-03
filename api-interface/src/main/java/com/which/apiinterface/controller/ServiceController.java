package com.which.apiinterface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.which.apiinterface.utils.RequestUtils.get;

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

    @GetMapping("/poisonousChickenSoup")
    public String getPoisonousChickenSoup() {
        return get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
    }

}
