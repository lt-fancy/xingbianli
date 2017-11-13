package com.sawallianc.rack.api;

import com.sawallianc.springboot.advice.WebApiAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/rack")
public class RackApi extends WebApiAdvice{
}
