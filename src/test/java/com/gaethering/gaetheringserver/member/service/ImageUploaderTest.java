package com.gaethering.gaetheringserver.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.gaethering.gaetheringserver.member.config.AwsS3MockConfig;
import com.gaethering.gaetheringserver.pet.exception.InvalidImageTypeException;
import com.gaethering.gaetheringserver.pet.exception.errorcode.PetErrorCode;
import com.gaethering.gaetheringserver.pet.service.ImageUploaderImpl;
import io.findify.s3mock.S3Mock;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;

@Import(AwsS3MockConfig.class)
@SpringBootTest(properties = "spring.config.location=" +
	"classpath:/application-test.yml"
)
public class ImageUploaderTest {

	@Autowired
	S3Mock s3Mock;

	@Autowired
	ImageUploaderImpl imageUploader;

	@Value("${test-path}")
	private String testPath;

	@AfterEach
	public void shutdownMockS3() {
		s3Mock.stop();
	}

	@Test
	void uploadPetImageFailure_fileExtension() throws IOException {
		// given
		String filename = "test.txt";
		String contentType = "image/png";

		MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
			"test".getBytes());

		// when
		InvalidImageTypeException exception = Assertions.assertThrows(
			InvalidImageTypeException.class,
			() -> imageUploader.uploadImage(file));

		// then
		assertThat(exception.getErrorCode()).isEqualTo(PetErrorCode.INVALID_IMAGE_TYPE);
	}

	@Test
	void uploadPetImageSuccess() throws IOException {
		// given
		String filename = "test.png";
		String contentType = "image/png";

		String path = testPath + "/test-bucket/test-dir/";

		MockMultipartFile file = new MockMultipartFile("test", filename, contentType,
			"test".getBytes());

		// when
		String urlPath = imageUploader.uploadImage(file);

		// then
		assertThat(urlPath.substring(0, urlPath.lastIndexOf("/") + 1)).isEqualTo(path);
	}

}
