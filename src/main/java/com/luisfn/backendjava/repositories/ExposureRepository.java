package com.luisfn.backendjava.repositories;

import com.luisfn.backendjava.entities.ExposureEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExposureRepository extends CrudRepository<ExposureEntity, Long> {
    public ExposureEntity findById(long id);
}
