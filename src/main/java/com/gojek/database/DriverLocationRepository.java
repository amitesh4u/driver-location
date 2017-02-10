package com.gojek.database;

import com.gojek.domain.DriverLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Amitesh on 09-02-2017.
 */
/* MongoRepository<CLASS, ID Type> */
public interface DriverLocationRepository extends MongoRepository<DriverLocation, Integer> {

    List<DriverLocation> findByAtGreaterThan(LocalDateTime at);
}
