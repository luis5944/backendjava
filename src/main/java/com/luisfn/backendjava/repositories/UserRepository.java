package com.luisfn.backendjava.repositories;

import com.luisfn.backendjava.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    public UserEntity findByEmail(String email);


}
