package com.busana.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/* 
    ===============================================================
    HOW TO CREATE A CONTROLLER

    1. @Controller              -> marks this as a Spring MVC Controller
                                    (use @RestController only if returning JSON, not views)
    
    2. @GetMapping("/path")     -> handles GET requests to that URL path
       @PostMapping("/path")    -> handles POST requests (form submissions)
    
    3. Model model              -> used to pass data from controller to the HTML template
       model.addAttribute("key", value) -> access in HTML as ${key}
    
    4. return "folder/filename" -> renders src/main/resources/templates/folder/filename.html
       e.g. return "customer/home"  -> renders templates/customer/home.html

    How flow works:
        Browser visits URL -> Controller method runs -> adds data to Model
        -> returns template name -> Thymeleaf renders HTML -> browser displays page
    ===============================================================
*/

@Controller
public class TestController {
    
    // visit http://localhost:8080/ to test this
    @GetMapping("/")
    public String home(Model model) {
        // pass data to HTML template
        model.addAttribute("pageTitle", "Welcome to BUSANA.my");
        model.addAttribute("successMessage", "SpringBoot + Thymeleaf + MySQL is working correctly!");

        // returns templates/customer/home.html
        return "customer/home";
    }
    
    @GetMapping("/test")
    public String test(@RequestParam Model model) {
        // visit http://localhost:8080/test to see a plain confirmation
        model.addAttribute("pageTitle", "Stack Test");
        model.addAttribute("successMessage", "Controller -> Service -> Repository chain is ready");
        return "customer/home";
    }
    
}
