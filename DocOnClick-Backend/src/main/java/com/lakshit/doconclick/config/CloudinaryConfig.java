package com.lakshit.doconclick.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {
	

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }
}


/*    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dkqvrmxvh",
            "api_key", "293643239753871",
            "api_secret", "p7-qvL0AzGnkFBXPUd-Ui_u1H_g"
        ));
    }
}
*/
//ObjectUtils is a utility class that provides helper methods for working with objects

/* Spring Boot + Cloudinary integration
 * -----------------------------------
 * In this context, ObjectUtils.asMap(...) is not from Spring — 
 * it's from Cloudinary’s Java SDK, specifically:
 * import com.cloudinary.utils.ObjectUtils;
 * This method helps you easily construct a Map using key-value pairs, which is required by the 
 * Cloudinary constructor to initialize the config.
*/

/*
 * Map config = new HashMap();
 * config.put("cloud_name", "...");
 * config.put("api_key", "...");
 * config.put("api_secret", "...");
 * But
 * ObjectUtils.asMap(...) simplifies it to a one-liner.
 */


