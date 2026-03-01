package com.rev.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path audioDir = Paths.get(System.getProperty("user.dir"), "uploads", "audio");
        String audioPath = audioDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/audio/**")
                .addResourceLocations("file:///" + audioPath.replace("\\", "/") + "/");

        Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + uploadPath.replace("\\", "/") + "/");
    }
}
