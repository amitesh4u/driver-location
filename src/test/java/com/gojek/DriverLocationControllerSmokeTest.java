package com.gojek;

import static org.assertj.core.api.Assertions.assertThat;

import com.gojek.controller.DriverLocationController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

/**
 * Created by Amitesh on 11-02-2017.
 */
public class DriverLocationControllerSmokeTest {

    @Autowired
    DriverLocationController driverLocationController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(driverLocationController).isNotNull();
    }
}
