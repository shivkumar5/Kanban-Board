package com.example.kanban;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloContoller {

    @GetMapping("/hello")
    public String sayHello(){
        return  "Hello, this is my 1st spring boot api!!!";
    }
}
