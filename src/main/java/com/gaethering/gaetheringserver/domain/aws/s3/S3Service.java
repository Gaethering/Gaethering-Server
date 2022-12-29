package com.gaethering.gaetheringserver.domain.aws.s3;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String uploadImage(MultipartFile multipartFile, String dir);

    void removeImage(String filename, String dir);

}
