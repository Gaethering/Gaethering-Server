package com.gaethering.gaetheringserver.domain.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gaethering.gaetheringserver.core.type.FileExtension;
import com.gaethering.gaetheringserver.domain.pet.exception.FailedUploadImageException;
import com.gaethering.gaetheringserver.domain.pet.exception.ImageNotFoundException;
import com.gaethering.gaetheringserver.domain.pet.exception.InvalidImageTypeException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class S3ServiceImpl implements S3Service {

    private final AmazonS3 amazonS3;
    private final String bucket;
//    private final String dir;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3,
                         @Value("${cloud.aws.s3.bucket}") String bucket/*,
                         @Value("${dir}") String dir*/) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
//        this.dir = dir;
    }

    @Override
    public String uploadImage(MultipartFile multipartFile, String dir) {

        String fileName = createFileName(multipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(new PutObjectRequest(
                    bucket + "/" + dir, fileName, multipartFile.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (IOException e) {
            throw new FailedUploadImageException();
        }

        return amazonS3.getUrl(bucket, dir + "/" + fileName).toString();
    }

    @Override
    public void removeImage(String filename, String dir) {
        amazonS3.deleteObject(bucket,
                dir + "/" + filename.substring(filename.lastIndexOf("/") + 1));
    }

    private String createFileName(String filename) {
        return UUID.randomUUID().toString().concat(getFileExtension(filename));
    }

    private String getFileExtension(String filename) {
        if (filename.length() == 0) {
            throw new ImageNotFoundException();
        }
        validateFileExtension(filename);

        return filename.substring(filename.lastIndexOf("."));
    }

    private void validateFileExtension(String filename) {
        List<String> fileValidate = Stream.of(FileExtension.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        String idxFileName = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

        if (!fileValidate.contains(idxFileName)) {
            throw new InvalidImageTypeException();
        }
    }

}
