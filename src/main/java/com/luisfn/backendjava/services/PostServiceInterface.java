package com.luisfn.backendjava.services;

import com.luisfn.backendjava.shared.dto.PostCreationDto;
import com.luisfn.backendjava.shared.dto.PostDto;

import java.util.List;

public interface PostServiceInterface {
    public PostDto createPost(PostCreationDto post);
    public List<PostDto> getLastPosts();
    public PostDto getPost(String postId);
    public void deletePost(String postId, long userId);
    public PostDto updatePost(String postId, long userId, PostCreationDto postUpdateDto);

}
