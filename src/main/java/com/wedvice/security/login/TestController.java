package com.wedvice.security.login;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("/test")
    public String testController(@LoginUser CustomUserDetails customUserDetails) {

        System.out.println("customUserDetails = " + customUserDetails.toString());
        System.out.println("customUserDetails = " + customUserDetails);

        System.out.println("customUserDetails = " + customUserDetails);
        CustomUserDetails customUserDetails1 = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("customUserDetails1 = " + customUserDetails1);



        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
