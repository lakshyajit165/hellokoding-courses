package com.hellokoding.auth.web;

import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.SecurityService;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserValidator userValidator;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPassword());

        return "redirect:/welcome";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {

        System.out.println("+++++++++++++" +SecurityContextHolder.getContext().getAuthentication().getPrincipal());
//        System.out.println(Arrays.toString(SecurityContextHolder.getContext().getAuthentication().getDetails().toString().split(":")));

        String[] sessionDetails = SecurityContextHolder.getContext().getAuthentication().getDetails().toString().split(":");


        // System.out.println("++++++++++++++++++ " + ((UserDetails)userDetails).getUsername());

        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        if(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser"))
            return "login";
        else
            return "redirect:/welcome";


    }


    @GetMapping({"/", "/welcome"})
    public String welcome(Model model) {
        Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        System.out.println(authorities);
        return "welcome";
    }


    @GetMapping("/about")
    public String about(Model model){
        return "about";
    }

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> testRouteForAdmin() {
        HashMap<String, String> mymap = new HashMap<>();
        mymap.put("Access", "ADMIN");

        return ResponseEntity.ok().body(mymap);
    }

}
