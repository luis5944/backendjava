package com.luisfn.backendjava.services;

import com.luisfn.backendjava.entities.PostEntity;
import com.luisfn.backendjava.exceptions.EmailExistsException;
import com.luisfn.backendjava.repositories.PostRepository;
import com.luisfn.backendjava.repositories.UserRepository;
import com.luisfn.backendjava.entities.UserEntity;
import com.luisfn.backendjava.shared.dto.PostDto;
import com.luisfn.backendjava.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserServiceInterface {
    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ModelMapper mapper;


    @Override
    public UserDto createUser(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new EmailExistsException("El email ya existe.");
        }

        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(user, userEntity);

        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UUID userId = UUID.randomUUID();

        userEntity.setUserId(userId.toString());


        UserEntity storedUser = userRepository.save(userEntity);

        UserDto userToReturn = new UserDto();
        BeanUtils.copyProperties(storedUser, userToReturn);

        return userToReturn;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        UserDto userToReturn = new UserDto();
        BeanUtils.copyProperties(userEntity, userToReturn);

        return userToReturn;
    }

    @Override
    public List<PostDto> getUserPosts(String email) {
        UserEntity user = userRepository.findByEmail(email);

        List<PostEntity> posts = postRepository.getByUserIdOrderByCreatedAtDesc(user.getId());
        List<PostDto> postToReturn = new ArrayList<>();

        posts.forEach((post) -> {
            PostDto postDto = mapper.map(post, PostDto.class);
            postToReturn.add(postDto);
        });

        return postToReturn;
    }

}
