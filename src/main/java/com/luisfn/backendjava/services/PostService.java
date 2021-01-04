package com.luisfn.backendjava.services;

import com.luisfn.backendjava.entities.ExposureEntity;
import com.luisfn.backendjava.entities.PostEntity;
import com.luisfn.backendjava.entities.UserEntity;
import com.luisfn.backendjava.repositories.ExposureRepository;
import com.luisfn.backendjava.repositories.PostRepository;
import com.luisfn.backendjava.repositories.UserRepository;
import com.luisfn.backendjava.shared.dto.PostCreationDto;
import com.luisfn.backendjava.shared.dto.PostDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PostService implements PostServiceInterface {
    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ExposureRepository exposureRepository;

    @Autowired
    ModelMapper mapper;

    @Override
    public PostDto createPost(PostCreationDto post) {
        UserEntity userEntity = userRepository.findByEmail(post.getUserEmail());
        ExposureEntity exposureEntity = exposureRepository.findById(post.getExposureId());

        PostEntity postEntity = new PostEntity();

        postEntity.setUser(userEntity);
        postEntity.setExposure(exposureEntity);
        postEntity.setTitle(post.getTitle());
        postEntity.setContent(post.getContent());
        postEntity.setPostId(UUID.randomUUID().toString());
        postEntity.setExpiresAt(new Date(System.currentTimeMillis() + (post.getExpirationTime() * 60000)));

        PostEntity createdPost = postRepository.save(postEntity);

        ModelMapper mapper = new ModelMapper();
        PostDto postToReturn = mapper.map(createdPost, PostDto.class);

        return postToReturn;
    }

    @Override
    public List<PostDto> getLastPosts() {
        long publicExposureId = 2;
        List<PostEntity> postEntities = postRepository.getLastPublicPosts(publicExposureId, new Date(System.currentTimeMillis()));

        List<PostDto> postsToReturn = new ArrayList<>();

        postEntities.forEach(p -> postsToReturn.add(mapper.map(p, PostDto.class)));
        return postsToReturn;
    }

    @Override
    public PostDto getPost(String postId) {

        PostEntity post = postRepository.findByPostId(postId);

        PostDto postToReturn = mapper.map(post, PostDto.class);

        return postToReturn;
    }

    @Override
    public void deletePost(String postId, long userId) {

        PostEntity post = postRepository.findByPostId(postId);

        if (post.getUser().getId() != userId) {
            throw new RuntimeException("No tienes permisos para borrar");
        }

        postRepository.delete(post);


    }

    @Override
    public PostDto updatePost(String postId, long userId, PostCreationDto postUpdateDto) {
        PostEntity postEntity = postRepository.findByPostId(postId);

        if (postEntity.getUser().getId() != userId) {
            throw new RuntimeException("No tienes permisos");
        }
        ExposureEntity exposureEntity = exposureRepository.findById(postUpdateDto.getExposureId());

        postEntity.setExposure(exposureEntity);
        postEntity.setTitle(postUpdateDto.getTitle());
        postEntity.setContent(postUpdateDto.getContent());
        postEntity.setExpiresAt(new Date(System.currentTimeMillis() + (postUpdateDto.getExpirationTime() * 60000)));

        PostEntity updatedPost = postRepository.save(postEntity);

        PostDto postToReturn = mapper.map(updatedPost, PostDto.class);

        return postToReturn;
    }
}
