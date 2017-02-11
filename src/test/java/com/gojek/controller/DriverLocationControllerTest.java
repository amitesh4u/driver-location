package com.gojek.controller;

import com.gojek.SpringMongoConfiguration;
import com.gojek.config.ValidationConfig;
import com.gojek.database.DriverLocationRepository;
import com.gojek.domain.DriverLocation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Created by Amitesh on 10-02-2017.
 */

/**
 * Testing class for Driver Location Controller
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc

public class DriverLocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ValidationConfig vc;

    @Autowired
    private DriverLocationRepository driverLocationRepository;

    private static List<DriverLocation> driverLocations;
    private static String ERR_LATITUDE, ERR_LONGITUDE, ERR_ACCURACY;
    private static boolean INIT_IS_DONE = false;

    @Before
    public void init() throws Exception {
        if (INIT_IS_DONE) { // To DO the initialization only once for all testes
            return;
        }
        // do the initialization
        ERR_LATITUDE = "Latitude should be between " + vc.getLatitudeMin() + " and " + vc.getLatitudeMax();
        ERR_LONGITUDE = "Longitude should be between " + vc.getLongitudeMin() + " and " + vc.getLongitudeMax();
        ERR_ACCURACY = "Accuracy should be between " + vc.getAccuracyMin() + " and " + vc.getAccuracyMax();

        /* Setup DB */
        driverLocationRepository.deleteAll();

        this.driverLocations = new ArrayList<DriverLocation>();
        double[] latitudes = {12.987654, 12.965687, 12.986568, 12.995657, 12.985659, 12.995651, 12.996553, 12.995655, 11.954655, 12.576665, 12.645437, 12.743437, 12.998677, 12.998687, 12.998697, 12.998607, 12.998657, 12.998677, 12.998677, 12.998697};
        double[] longitudes = {77.123456, 77.245455, 77.254555, 77.345865, 77.345555, 77.340436, 77.634835, 78.344441, 78.323532, 76.554867, 76.155455, 75.565645, 77.423464, 77.423464, 77.423464, 77.423454, 77.423474, 77.423564, 77.423664, 77.423474};
        double[] accuracies = {0.05, 0.06, 0.05, 0.055, 0.01, 0.02, 0.03, 0.5, 0.067, 0.08, 0.09, 0.0234, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0};

        /* Set the time to N min minus current time where N is length of data */
        LocalDateTime nowMinusNmin = LocalDateTime.now().minus(latitudes.length, ChronoUnit.MINUTES);
        /* Insert each row and increment time by 1 min */
        for (int i = 0; i < latitudes.length; i++) {
            this.driverLocations.add(new DriverLocation((i + 1), latitudes[i], longitudes[i], accuracies[i], nowMinusNmin.plus(i, ChronoUnit.MINUTES)));
        }

        driverLocationRepository.save(driverLocations);
        driverLocationRepository.findAll().forEach(System.out::println);

        INIT_IS_DONE = true;
    }

    @After
    public void destroy() throws Exception {
        //driverLocationRepository.deleteAll();
    }

    @Test
    public void setDriverLocation() throws Exception {
        int id = (int) (Math.random() + 1) * 100;
        double latitude = 12 + Math.random();
        double longitude = 77 + Math.random();
        double accuracy = Math.random();

        String body = String.format("{\"latitude\": %f,\"longitude\": %f,\"accuracy\": %f}", latitude, longitude, accuracy);
        System.out.println("PUT Body for Id: " + id + " | " + body);
        mockMvc.perform(put("/drivers/" + id + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{}")));

        /* Verify for DB entry */
        DriverLocation driverLocation = driverLocationRepository.findOne(id);
        Assert.assertEquals(driverLocation.getLatitude(), latitude, 0.001);
        Assert.assertEquals(driverLocation.getLongitude(), longitude, 0.001);
        Assert.assertEquals(driverLocation.getAccuracy(), accuracy, 0.001);

        driverLocationRepository.delete(driverLocation);
    }

    @Test
    public void setDriverLocationUserIdNotInteger() throws Exception {
        String id = "abc";
        double latitude = 12 + Math.random();
        double longitude = 77 + Math.random();
        double accuracy = Math.random();

        String body = String.format("{\"latitude\": %f,\"longitude\": %f,\"accuracy\": %f}", latitude, longitude, accuracy);
        System.out.println("PUT Body for Id: " + id + " | " + body);
        mockMvc.perform(put("/drivers/" + id + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void setDriverLocationUserIdOutOfRange() throws Exception {
        int id = 50000000;
        double latitude = 12 + Math.random();
        double longitude = 77 + Math.random();
        double accuracy = Math.random();

        String body = String.format("{\"latitude\": %f,\"longitude\": %f,\"accuracy\": %f}", latitude, longitude, accuracy);
        System.out.println("PUT Body for Id: " + id + " | " + body);
        mockMvc.perform(put("/drivers/" + id + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());

        /* Verify for No DB entry */
        DriverLocation driverLocation = driverLocationRepository.findOne(id);
        Assert.assertNull(driverLocation);
    }

    @Test
    public void setDriverLocationBodyNotPresent() throws Exception {
        int id = (int) (Math.random() + 1) * 100;

        String body = null;
        System.out.println("PUT Body for Id: " + id + " | " + body);
        mockMvc.perform(put("/drivers/" + id + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        /* Verify for No DB entry */
        DriverLocation driverLocation = driverLocationRepository.findOne(id);
        Assert.assertNull(driverLocation);
    }

    @Test
    public void setDriverLocationDataKeysNotPresent() throws Exception {
        int id = (int) (Math.random() + 1) * 100;

        String body = "{}";
        System.out.println("PUT Body for Id: " + id + " | " + body);
        mockMvc.perform(put("/drivers/" + id + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());

        /* Verify for No DB entry */
        DriverLocation driverLocation = driverLocationRepository.findOne(id);
        Assert.assertNull(driverLocation);
    }

    @Test
    public void setDriverLocationLatitudeIsOutOfRange() throws Exception {
        int id = (int) (Math.random() + 1) * 100;
        double latitude = 112 + Math.random();
        double longitude = 77 + Math.random();
        double accuracy = Math.random();

        String body = String.format("{\"latitude\": %f,\"longitude\": %f,\"accuracy\": %f}", latitude, longitude, accuracy);

        validatePUTErrorRequest(id, body, "{\"errors\":[\"" + ERR_LATITUDE + "\"]}");

    }

    @Test
    public void setDriverLocationLongitudeIsOutOfRange() throws Exception {
        int id = (int) (Math.random() + 1) * 100;
        double latitude = 12 + Math.random();
        double longitude = 277 + Math.random();
        double accuracy = Math.random();

        String body = String.format("{\"latitude\": %f,\"longitude\": %f,\"accuracy\": %f}", latitude, longitude, accuracy);

        validatePUTErrorRequest(id, body, "{\"errors\":[\"" + ERR_LONGITUDE + "\"]}");
    }

    @Test
    public void setDriverLocationAccuracyIsOutOfRange() throws Exception {
        int id = (int) (Math.random() + 1) * 100;
        double latitude = 12 + Math.random();
        double longitude = 77 + Math.random();
        double accuracy = (Math.random() + 0.11) * 10; // Added a number to make sure the random value is always greater than 1

        String body = String.format("{\"latitude\": %f,\"longitude\": %f,\"accuracy\": %f}", latitude, longitude, accuracy);

        validatePUTErrorRequest(id, body, "{\"errors\":[\"" + ERR_ACCURACY + "\"]}");
    }

    @Test
    public void setDriverLocationLatitudeLongitudeIsOutOfRange() throws Exception {
        int id = (int) (Math.random() + 1) * 100;
        double latitude = 112 + Math.random();
        double longitude = 277 + Math.random();
        double accuracy = Math.random();

        String body = String.format("{\"latitude\": %f,\"longitude\": %f,\"accuracy\": %f}", latitude, longitude, accuracy);

        validatePUTErrorRequest(id, body, "{\"errors\":[\"" + ERR_LATITUDE + "\",\"" + ERR_LONGITUDE + "\"]}");
    }

    @Test
    public void setDriverLocationLatitudeLongitudeAccuracyIsOutOfRange() throws Exception {
        int id = (int) (Math.random() + 1) * 100;
        double latitude = 112 + Math.random();
        double longitude = 277 + Math.random();
        double accuracy = (Math.random() + 0.11) * 10; // Added a number to make sure the random value is always greater than 1

        String body = String.format("{\"latitude\": %f,\"longitude\": %f,\"accuracy\": %f}", latitude, longitude, accuracy);

        validatePUTErrorRequest(id, body, "{\"errors\":[\"" + ERR_LATITUDE + "\",\"" + ERR_LONGITUDE + "\",\"" + ERR_ACCURACY + "\"]}");
    }


    public void validatePUTErrorRequest(int id, String body, String content) throws Exception {
        System.out.println("PUT Body for Id: " + id + " | " + body);
        System.out.println("Content: " + content);
        mockMvc.perform(put("/drivers/" + id + "/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.TEXT_PLAIN_VALUE))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(equalTo(content))); // TODO Replace equalTO with contains and check for each expected error separately

        /* Verify for No DB entry */
        DriverLocation driverLocation = driverLocationRepository.findOne(id);
        Assert.assertNull(driverLocation);
    }

    @Test
    public void getDriversAllValuesSet() throws Exception {
        mockMvc.perform(get("/drivers?latitude=12.998664&longitude=77.423466&radius=2000&limit=10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[{\"id\":20,\"distance\":3,\"latitude\":12.998697,\"longitude\":77.423474},{\"id\":13,\"distance\":1553,\"latitude\":12.998677,\"longitude\":77.423464},{\"id\":17,\"distance\":1553,\"latitude\":12.998657,\"longitude\":77.423474},{\"id\":14,\"distance\":1554,\"latitude\":12.998687,\"longitude\":77.423464},{\"id\":15,\"distance\":1555,\"latitude\":12.998697,\"longitude\":77.423464},{\"id\":16,\"distance\":1557,\"latitude\":12.998607,\"longitude\":77.423454},{\"id\":18,\"distance\":1560,\"latitude\":12.998677,\"longitude\":77.423564},{\"id\":19,\"distance\":1568,\"latitude\":12.998677,\"longitude\":77.423664}]")));
    }

    @Test
    public void getDriversOptionalLimitAndRadius() throws Exception {
        mockMvc.perform(get("/drivers?latitude=12.998664&longitude=77.423466")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[{\"id\":20,\"distance\":3,\"latitude\":12.998697,\"longitude\":77.423474}]")));
    }

    @Test
    public void getDriversOptionalLimit() throws Exception {
        mockMvc.perform(get("/drivers?latitude=12.998664&longitude=77.423466&radius=20000")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[{\"id\":20,\"distance\":3,\"latitude\":12.998697,\"longitude\":77.423474},{\"id\":13,\"distance\":1553,\"latitude\":12.998677,\"longitude\":77.423464},{\"id\":17,\"distance\":1553,\"latitude\":12.998657,\"longitude\":77.423474},{\"id\":14,\"distance\":1554,\"latitude\":12.998687,\"longitude\":77.423464},{\"id\":15,\"distance\":1555,\"latitude\":12.998697,\"longitude\":77.423464},{\"id\":16,\"distance\":1557,\"latitude\":12.998607,\"longitude\":77.423454},{\"id\":18,\"distance\":1560,\"latitude\":12.998677,\"longitude\":77.423564},{\"id\":19,\"distance\":1568,\"latitude\":12.998677,\"longitude\":77.423664},{\"id\":5,\"distance\":9862,\"latitude\":12.985659,\"longitude\":77.345555},{\"id\":6,\"distance\":11452,\"latitude\":12.995651,\"longitude\":77.340436}]")));
    }

    @Test
    public void getDriversOptionalRadius() throws Exception {
        mockMvc.perform(get("/drivers?latitude=12.998664&longitude=77.423466&limit=10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[{\"id\":20,\"distance\":3,\"latitude\":12.998697,\"longitude\":77.423474}]")));

    }

    @Test
    public void getDriversLatitudeLongitudeNotPresent() throws Exception {
        mockMvc.perform(get("/drivers?radius=2000&limit=10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("")));
    }

    @Test
    public void getDriversLatitudeOutOfRange() throws Exception {
        mockMvc.perform(get("/drivers?latitude=-91.998664&longitude=77.423466&radius=2000&limit=10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("{\"errors\":[\"" + ERR_LATITUDE + "\"]}")));
    }

    @Test
    public void getDriversLongitudeOutOfRange() throws Exception {
        mockMvc.perform(get("/drivers?latitude=12.998664&longitude=-277.423466&radius=2000&limit=10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("{\"errors\":[\"" + ERR_LONGITUDE + "\"]}")));
    }
}
