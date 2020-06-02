package com.foncanavari.fonApp.controller;


import com.foncanavari.fonApp.model.User;
import com.foncanavari.fonApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public String confirmUser(@RequestBody User user) {
        String message = userRepository.findByUser(user.getEmail(), user.getPass());
        if (StringUtils.isEmpty(message))
            return "FAIL";
        else
            return message;
    }

    @PostMapping
    public Boolean createUser(@RequestBody User user) {
        if (!StringUtils.isEmpty(userRepository.findUserByEmail(user.getEmail())))
            return false;
        user.setUser_type("USER");
        userRepository.save(user);
        return true; // buralara guvenlşk felan yapılır sora
    }

    @PutMapping
    public Boolean updatePassword(@RequestBody User user) {
        User user_u = userRepository.findUserByEmail(user.getEmail());

        user_u.setPass(user.getPass());
        userRepository.save(user_u);
        return true;
    }

}
