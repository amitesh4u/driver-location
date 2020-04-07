package com.amitesh.database;

import com.amitesh.domain.DriverLocation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Created by Amitesh on 09-02-2017.
 */

/**
 * Declare additional methods related to DriverLocation document.
 */
/* MongoRepository<CLASS, ID Type> */
public interface DriverLocationRepository extends MongoRepository<DriverLocation, Integer> {

    /**
     * Fetch All Driver Locations present within NOW-At time limit
     * @param at
     * @return List<DriverLocation>
     */
    List<DriverLocation> findByAtGreaterThan(LocalDateTime at);
}
