package com.yzc.bankingapp.controller;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Api(value = "Custom Swagger UI", description = "Custom Swagger UI Endpoint")
public class SwaggerController {

    @RequestMapping("/custom-swagger")
    public String customSwagger() {
        return "redirect:/swagger-ui.html";
    }

}
