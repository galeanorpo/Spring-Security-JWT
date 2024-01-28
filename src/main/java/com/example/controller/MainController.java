package com.example.controller;

import com.example.controller.request.CreateUserDto;
import com.example.models.ERole;
import com.example.models.RoleEntity;
import com.example.models.UserEntity;
import com.example.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class MainController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/hello")
    public String hello(){
        return "hello world not secure";
    }

    @GetMapping("/helloSecure")
    public String helloSecure(){
        return "hello world secure";
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDto createUserDto){

        Set<RoleEntity> roles = createUserDto.getRoles().stream()
                .map(role -> RoleEntity.builder()
                        .name(ERole.valueOf(role))
                        .build())
                .collect(Collectors.toSet());

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDto.getUsername())
                .password(this.passwordEncoder.encode(createUserDto.getPassword()))
                .email(createUserDto.getEmail())
                .roles(roles)
                .build();

        this.userRepository.save(userEntity);

        return ResponseEntity.ok(userEntity);
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String id){
        if(this.userRepository.existsById(Long.parseLong(id))){
            this.userRepository.deleteById(Long.parseLong(id));
            return "User delete succesfull ".concat(id);
        }
        return "Upssss.... have a error with id ".concat(id);
    }
}
