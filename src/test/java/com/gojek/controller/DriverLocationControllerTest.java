package com.gojek.controller;

import com.gojek.SpringMongoConfiguration;
import com.gojek.database.DriverLocationRepository;
import com.gojek.domain.DriverLocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amitesh on 10-02-2017.
 */

/**
 * Testing class for Driver Location Controller
 */

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {
        SpringMongoConfiguration.class // Optional but better to add for clarity
})
public class DriverLocationControllerTest {

    private MockMvc mockMvc;

    private List<DriverLocation> driverLocations;

    @Autowired
    private DriverLocationRepository driverLocationRepository;

    @Before
    public void setup() throws Exception {

       // driverLocationRepository.deleteAll();

        this.driverLocations = new ArrayList<DriverLocation>();
        double[] latitudes = {12.987654,12.965687,12.986568,12.995657,12.985659,12.995651,12.996553,12.995655,11.954655,12.576665,12.645437,12.743437,12.998677,12.998687,12.998697,12.998607,12.998657,12.998677,12.998677,12.998697};
        double[] longitudes = {77.123456,77.245455,77.254555,77.345865,77.345555,77.340436,77.634835,78.344441,78.323532,76.554867,76.155455,75.565645,77.423464,77.423464,77.423464,77.423454,77.423474,77.423564,77.423664,77.423474};
        double[] accuracies = {0.05,0.06,0.05,0.055,0.01,0.02,0.03,0.5,0.067,0.08,0.09,0.0234,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0};

        /* Set the time to N min minus current time where N is length of data */
        LocalDateTime nowMinusNmin = LocalDateTime.now().minus(latitudes.length, ChronoUnit.MINUTES);
        /* Insert each row and increment time by 1 min */
        for(int i = 0 ; i < latitudes.length ; i++){
            this.driverLocations.add(new DriverLocation((i+1), latitudes[i],longitudes[i], accuracies[i], nowMinusNmin.plus(i,ChronoUnit.MINUTES)));
        }

        driverLocationRepository.save(driverLocations);
        driverLocationRepository.findAll().forEach(System.out::println);
    }

    @After
    public void destroy() throws Exception {
       // driverLocationRepository.deleteAll();
    }

    @Test
    public void setDriverLocation() throws Exception {

    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void getDrivers() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/drivers")
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string(equalTo("{\"id\":1,\"content\":\"Hello, amitesh!\",\"msg\":\"\"}")));
//    }

//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }
//
//    @Test
//    public void setDriverLocation() throws Exception {
//
//    }


}
