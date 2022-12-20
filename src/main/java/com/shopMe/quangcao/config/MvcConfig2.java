package com.shopMe.quangcao.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig2 implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "web-images";
        String productName = "product-images";
        String addressName = "address-images";
        String userName = "user-images";

        Path userPhotosDir = Paths.get(dirName);
        String userPhotosPath = userPhotosDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:/" + userPhotosPath + "/");

        Path avatarDir = Paths.get(userName);
        String avatarPhotosPath = avatarDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/" + userName + "/**")
            .addResourceLocations("file:/" + avatarPhotosPath + "/");


        Path pathPrd = Paths.get(productName);
        String prdPhotoDir = pathPrd.toFile().getAbsolutePath();
        registry.addResourceHandler("/" + productName + "/**")
                .addResourceLocations("file:/" + prdPhotoDir + "/");



        Path pathAddess = Paths.get(addressName);
        String addressDir = pathAddess.toFile().getAbsolutePath();
        registry.addResourceHandler("/" + addressName + "/**")
                .addResourceLocations("file:/" + addressDir + "/");
    }

}