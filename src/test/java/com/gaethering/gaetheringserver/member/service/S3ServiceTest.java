package com.gaethering.gaetheringserver.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.gaethering.gaetheringserver.domain.aws.s3.S3ServiceImpl;
import com.gaethering.gaetheringserver.domain.pet.exception.InvalidImageTypeException;
import com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode;
import com.gaethering.gaetheringserver.member.config.AwsS3MockConfig;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

@Import(AwsS3MockConfig.class)
@SpringBootTest
@ActiveProfiles("test")
public class S3ServiceTest {

    @Autowired
    private S3Mock s3Mock;

    @Autowired
    private S3ServiceImpl s3Service;

    @Value("${test-path}")
    private String testPath;

    @AfterEach
    public void shutdownMockS3() {
        s3Mock.stop();
    }

    @Test
    void uploadPetImageFailure_fileExtension() {
        // given
        String filename = "test.txt";
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
            "test".getBytes());

        // when
        InvalidImageTypeException exception = Assertions.assertThrows(
            InvalidImageTypeException.class,
            () -> s3Service.uploadImage(file));

        // then
        assertThat(exception.getErrorCode()).isEqualTo(PetErrorCode.INVALID_IMAGE_TYPE);
    }

    @Test
    void uploadPetImageSuccess() {
        // given
        String filename = "test.png";
        String contentType = "image/png";

        String path = testPath + "/test-bucket/test-dir/";

        MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
            "test".getBytes());

        // when
        String urlPath = s3Service.uploadImage(file);

        // then
        assertThat(urlPath.substring(0, urlPath.lastIndexOf("/") + 1)).isEqualTo(path);
    }

}
