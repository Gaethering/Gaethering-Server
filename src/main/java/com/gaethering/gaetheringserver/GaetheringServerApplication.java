package com.gaethering.gaetheringserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GaetheringServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaetheringServerApplication.class, args);
    }

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

}
