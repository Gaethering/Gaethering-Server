package com.gaethering.gaetheringserver.pet.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {

    String uploadImage(MultipartFile multipartFile);

    void removeImage(String filename);

}
