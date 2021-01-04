package com.luisfn.backendjava.controllers;

import com.luisfn.backendjava.models.requests.PostCreateRequestModel;
import com.luisfn.backendjava.models.responses.OperationStatusModel;
import com.luisfn.backendjava.models.responses.PostRest;
import com.luisfn.backendjava.services.PostServiceInterface;
import com.luisfn.backendjava.services.UserServiceInterface;
import com.luisfn.backendjava.shared.dto.PostCreationDto;
import com.luisfn.backendjava.shared.dto.PostDto;
import com.luisfn.backendjava.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    PostServiceInterface postService;

    @Autowired
    ModelMapper mapper;
    @Autowired
    UserServiceInterface userService;

    @PostMapping
    public PostRest createPost(@RequestBody PostCreateRequestModel createRequestModel) {

        //Cogemos el email del usuario logueado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();

        //Copiamos los datos que vienen del body al objeto postcreationDto
        PostCreationDto postCreationDto = mapper.map(createRequestModel, PostCreationDto.class);

        //Además, le metemos el email
        postCreationDto.setUserEmail(email);

        //Creamos el post
        PostDto postDto = postService.createPost(postCreationDto);

        //Creamos el post que retornamos
        PostRest postToReturn = mapper.map(postDto, PostRest.class);

        //Cambiar el expired a true
        if (postToReturn.getExpiresAt().compareTo(new Date(System.currentTimeMillis())) < 0) {
            postToReturn.setExpired(true);
        }

        return postToReturn;
    }

    @GetMapping(path = "/last")
    public List<PostRest> lastPosts() {
        List<PostDto> posts = postService.getLastPosts();
        List<PostRest> postsToReturn = new ArrayList<>();

        posts.forEach((post) -> postsToReturn.add(mapper.map(post, PostRest.class)));


        return postsToReturn;
    }

    @GetMapping(path = "/{id}")
    public PostRest getPost(@PathVariable String id) {
        PostDto post = postService.getPost(id);
        PostRest postRest = mapper.map(post, PostRest.class);

        if (postRest.getExpiresAt().compareTo(new Date(System.currentTimeMillis())) < 0) {
            postRest.setExpired(true);
        }

        if (postRest.getExposure().getId() == 1 || postRest.isExpired()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDto user = userService.getUser(authentication.getPrincipal().toString());

            if (user.getId() != post.getUser().getId()) {
                throw new RuntimeException("No permisos.");
            }
        }

        return postRest;
    }

    @DeleteMapping(path = "/{id}")
    public OperationStatusModel deletePost(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto user = userService.getUser(authentication.getPrincipal().toString());

        OperationStatusModel modelOp = new OperationStatusModel();
        modelOp.setName("Delete");
        postService.deletePost(id, user.getId());
        modelOp.setResult("Success");

        return modelOp;
    }

    @PutMapping(path = "/{id}")
    public PostRest updatePost(@RequestBody PostCreateRequestModel postCreateRequestModel, @PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto user = userService.getUser(authentication.getPrincipal().toString());

        PostCreationDto postUpdateDto = mapper.map(postCreateRequestModel, PostCreationDto.class);

        PostDto updatedPost = postService.updatePost(id, user.getId(), postUpdateDto);

        PostRest postUpdatedToReturn = mapper.map(updatedPost, PostRest.class);

        return postUpdatedToReturn;
    }
}
