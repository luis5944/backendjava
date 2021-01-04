package com.luisfn.backendjava.controllers;


import com.luisfn.backendjava.models.requests.UserDetailsRequestModel;
import com.luisfn.backendjava.models.responses.PostRest;
import com.luisfn.backendjava.models.responses.UserRest;
import com.luisfn.backendjava.services.UserServiceInterface;
import com.luisfn.backendjava.shared.dto.PostDto;
import com.luisfn.backendjava.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    //Creamos esto para no hacer instancias
    @Autowired
    UserServiceInterface userService;
    @Autowired
    ModelMapper mapper;

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public UserRest getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();
        UserDto user = userService.getUser(email);


        UserRest userToReturn = mapper.map(user, UserRest.class);

        return userToReturn;
    }

    @PostMapping()
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        //Esto es lo que vamos a devolver
        UserRest user = new UserRest();
        //Esto es lo de la bbdd
        UserDto userDto = new UserDto();
        //Copiamos lo que viene del body y se lo metemos al objeto de la bbdd
        BeanUtils.copyProperties(userDetails, userDto);
        //Este es el usuario ya creado en la bbdd
        UserDto createdUser = userService.createUser(userDto);
        //Metemos el usuario creado y lo metemos al user que vamos a devolver
        BeanUtils.copyProperties(createdUser, user);

        return user;
    }

    @GetMapping(path = "/posts")
    public List<PostRest> getPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        List<PostDto> posts = userService.getUserPosts(email);
        List<PostRest> postsToReturn = new ArrayList<>();

        posts.forEach((post) -> {
            PostRest postRest = mapper.map(post, PostRest.class);

            //Cambiar el expired a true
            if (postRest.getExpiresAt().compareTo(new Date(System.currentTimeMillis())) < 0) {
                postRest.setExpired(true);
            }
            postsToReturn.add(postRest);
        });


        return postsToReturn;

    }
}
