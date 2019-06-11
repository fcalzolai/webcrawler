package com.webcrawler;

import org.assertj.core.api.BDDAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebcrawlerApplicationIT {

    @LocalServerPort
    private int port;

    @Test
    public void test_should_IsActive_return_active() {
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> entity = template.getForEntity("http://localhost:"+port+"/isActive", String.class);

        BDDAssertions.then(entity.getStatusCodeValue()).isEqualTo(200);
        BDDAssertions.then(entity.getBody()).isEqualTo("ACTIVE");
    }

}
