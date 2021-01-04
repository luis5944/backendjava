package com.luisfn.backendjava.services;

import com.luisfn.backendjava.shared.dto.PostDto;
import com.luisfn.backendjava.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserServiceInterface extends UserDetailsService {

    public UserDto createUser(UserDto user);

    public UserDto getUser(String email);

    public List<PostDto> getUserPosts(String email);

}
