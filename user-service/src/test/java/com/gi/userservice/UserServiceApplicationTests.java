package com.gi.userservice;

import com.gi.userservice.UserServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = UserServiceApplication.class)
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false",
    "security.enabled=false"
})
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}