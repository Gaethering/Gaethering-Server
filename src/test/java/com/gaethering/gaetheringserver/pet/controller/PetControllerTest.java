package com.gaethering.gaetheringserver.pet.controller;

import static com.gaethering.gaetheringserver.domain.member.exception.errorcode.MemberErrorCode.MEMBER_NOT_FOUND;
import static com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode.EXCEED_REGISTRABLE_PET;
import static com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode.FAILED_DELETE_PET;
import static com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode.FAILED_DELETE_REPRESENTATIVE;
import static com.gaethering.gaetheringserver.domain.pet.exception.errorcode.PetErrorCode.PET_NOT_FOUND;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentRequest;
import static com.gaethering.gaetheringserver.member.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.relaxedRequestParts;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaethering.gaetheringserver.config.SecurityConfig;
import com.gaethering.gaetheringserver.core.type.Gender;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.jwt.JwtAuthenticationFilter;
import com.gaethering.gaetheringserver.domain.pet.controller.PetController;
import com.gaethering.gaetheringserver.domain.pet.dto.PetImageUpdateResponse;
import com.gaethering.gaetheringserver.domain.pet.dto.PetProfileResponse;
import com.gaethering.gaetheringserver.domain.pet.dto.PetProfileUpdateRequest;
import com.gaethering.gaetheringserver.domain.pet.dto.PetRegisterRequest;
import com.gaethering.gaetheringserver.domain.pet.dto.PetRegisterResponse;
import com.gaethering.gaetheringserver.domain.pet.exception.ExceedRegistrablePetException;
import com.gaethering.gaetheringserver.domain.pet.exception.FailedDeletePetException;
import com.gaethering.gaetheringserver.domain.pet.exception.FailedDeleteRepresentativeException;
import com.gaethering.gaetheringserver.domain.pet.exception.PetNotFoundException;
import com.gaethering.gaetheringserver.domain.pet.service.PetService;
import java.security.Principal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = PetController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class})
})
@ActiveProfiles("test")
@AutoConfigureRestDocs
class PetControllerTest {

    @MockBean
    private PetService petService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public void setRepresentativePet() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        //when
        //then
        mockMvc.perform(patch("/api/mypage/pets/1/representative")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("펫 등록 성공")
    @WithMockUser
    void petRegister_Success() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();

        MockMultipartFile image = new MockMultipartFile("image", "test.png",
            "image/png", "test".getBytes());

        String requestString = objectMapper.writeValueAsString(request);

        MockMultipartFile data = new MockMultipartFile("data", "",
            "application/json", requestString.getBytes());

        given(petService.registerPet(anyString(), any(), any()))
            .willReturn(PetRegisterResponse.builder()
                .imageUrl(image.getName())
                .petName(request.getPetName())
                .build());

        //when
        //then
        mockMvc.perform(
                multipart("/api/pets/register")
                    .file(image)
                    .file(data)
                    .header("Authorization", "accessToken")
                    .with(csrf())
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.petName").value(request.getPetName()))
            .andExpect(jsonPath("$.imageUrl").value(image.getName()))
            .andDo(print())
            .andDo(document("pet/register/success",
                relaxedRequestParts(
                    partWithName("image").description("펫 프로필 사진"),
                    partWithName("data").description("펫 등록에 필요한 데이터")
                )
            ))
            .andDo(document("pet/register/success",
                requestPartFields("data",
                    fieldWithPath("petName").type(JsonFieldType.STRING).description("반려동물 이름"),
                    fieldWithPath("petBirth").type(JsonFieldType.STRING).description("반려동물 생일"),
                    fieldWithPath("breed").type(JsonFieldType.STRING).description("반려동물 견종"),
                    fieldWithPath("weight").type(JsonFieldType.NUMBER).description("반려동물 몸무게"),
                    fieldWithPath("petGender").type(JsonFieldType.STRING).description("반려동물 성별"),
                    fieldWithPath("description").type(JsonFieldType.STRING).description("반려동물 설명"),
                    fieldWithPath("isNeutered").type(JsonFieldType.BOOLEAN)
                        .description("반려동물 중성화 여부")
                )
            ))
            .andDo(document("pet/register/success",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token")
                )
            ));
    }

    @Test
    @DisplayName("펫 등록 실패_최대 등록 수를 넘은 경우")
    @WithMockUser
    void petRegister_Failure_ExceedRegistrablePet() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        PetRegisterRequest request = PetRegisterRequest.builder()
            .petName("뽀삐")
            .petBirth("2022-03-15")
            .weight(5.5f)
            .breed("말티즈")
            .petGender("FEMALE")
            .description("___")
            .neutered(true)
            .build();

        String filename = "test.png";
        String contentType = "image/png";

        MockMultipartFile image = new MockMultipartFile("image", filename, contentType,
            "test".getBytes());

        String requestString = objectMapper.writeValueAsString(request);

        MockMultipartFile data = new MockMultipartFile("data", "",
            "application/json", requestString.getBytes());

        given(petService.registerPet(anyString(), any(), any()))
            .willThrow(new ExceedRegistrablePetException());

        //when
        //then
        mockMvc.perform(
                multipart("/api/pets/register")
                    .file(image)
                    .file(data)
                    .header("Authorization", "accessToken")
                    .with(csrf())
            ).andDo(print())
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.code").value(EXCEED_REGISTRABLE_PET.getCode()))
            .andExpect(jsonPath("$.message").value(EXCEED_REGISTRABLE_PET.getMessage()))
            .andDo(print())
            .andDo(document("pet/register/failure/exceed-max",
                relaxedRequestParts(
                    partWithName("image").description("펫 프로필 사진"),
                    partWithName("data").description("펫 등록에 필요한 데이터")
                )
            ))
            .andDo(document("pet/register/failure/exceed-max",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 이미지 수정 성공")
    void updatePetImage_Success() throws Exception {
        // given
        String filename = "test.png";
        String contentType = "image/png";

        MockMultipartFile image = new MockMultipartFile("image", filename, contentType,
            "test".getBytes());
        PetImageUpdateResponse response = PetImageUpdateResponse.builder()
            .imageUrl("https://test").build();

        given(petService.updatePetImage(1L, image))
            .willReturn(PetImageUpdateResponse.builder().imageUrl(response.getImageUrl()).build());

        // when
        // then
        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/api/mypage/pets/{petId}/image", 1)
                    .file(image)
                    .with(csrf())
                    .header("Authorization", "accessToken")
                    .with(request -> {
                        request.setMethod("PATCH");
                        return request;
                    }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.imageUrl").value(response.getImageUrl()))
            .andDo(print())
            .andDo(document("pet/update-pet-image/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("반려동물 프로필 Id")),
                relaxedRequestParts(
                    partWithName("image").description("펫 프로필 사진")
                ),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 이미지 수정 실패-반려동물 없을때")
    void updatePetImageFailure_PetNotFound() throws Exception {
        // given
        String filename = "test.png";
        String contentType = "image/png";

        MockMultipartFile image = new MockMultipartFile("image", filename, contentType,
            "test".getBytes());

        given(petService.updatePetImage(1L, image))
            .willThrow(new PetNotFoundException());

        mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/api/mypage/pets/{petId}/image", 1)
                    .file(image)
                    .with(csrf())
                    .header("Authorization", "accessToken")
                    .with(request -> {
                        request.setMethod("PATCH");
                        return request;
                    }))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(PET_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(PET_NOT_FOUND.getMessage()))
            .andDo(print())
            .andDo(document("pet/update-pet-image/failure/pet-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("반려동물 프로필 Id")),
                relaxedRequestParts(
                    partWithName("image").description("펫 프로필 사진")
                ),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 조회 성공")
    public void getPetProfile_Success() throws Exception {
        //given
        PetProfileResponse petProfile = PetProfileResponse.builder().name("해")
            .birth(LocalDate.parse("2021-12-01"))
            .gender(Gender.valueOf("FEMALE"))
            .breed("말티즈")
            .weight(3.6f)
            .neutered(true)
            .description("하얘요")
            .imageUrl("https://test").build();
        given(petService.getPetProfile(anyLong())).willReturn(petProfile);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/pets/{petId}/profile", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(petProfile.getName()))
            .andExpect(jsonPath("$.birth").value(String.valueOf(petProfile.getBirth())))
            .andExpect(jsonPath("$.gender").value(String.valueOf(petProfile.getGender())))
            .andExpect(jsonPath("$.breed").value(petProfile.getBreed()))
            .andExpect(jsonPath("$.isNeutered").value(petProfile.isNeutered()))
            .andExpect(jsonPath("$.weight").value(petProfile.getWeight()))
            .andExpect(jsonPath("$.description").value(petProfile.getDescription()))
            .andExpect(jsonPath("$.imageUrl").value(petProfile.getImageUrl()))
            .andDo(print())
            .andDo(document("pet/get-pet-profile/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("조회할 반려동물 프로필 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 조회 실패-반려동물 없을때")
    void getPetProfileFailure_PetNotFound() throws Exception {
        // given
        given(petService.getPetProfile(anyLong())).willThrow(new PetNotFoundException());

        // when
        // then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/pets/{petId}/profile", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(PET_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(PET_NOT_FOUND.getMessage()))
            .andDo(print())
            .andDo(document("pet/get-pet-profile/failure/pet-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("조회할 반려동물 프로필 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 수정 성공")
    public void updatePetProfile_Success() throws Exception {
        //given
        PetProfileUpdateRequest request = PetProfileUpdateRequest.builder()
            .weight(3.5f)
            .neutered(true)
            .description("귀여워요")
            .build();
        PetProfileResponse response = PetProfileResponse.builder()
            .name("해")
            .birth(LocalDate.parse("2021-12-01"))
            .gender(Gender.valueOf("FEMALE"))
            .breed("말티즈")
            .weight(3.5f)
            .neutered(true)
            .description("귀여워요")
            .imageUrl("https://test")
            .build();
        given(petService.updatePetProfile(anyLong(), any()))
            .willReturn(response);

        String requestString = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/mypage/pets/{petId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken")
                .content(requestString))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(response.getName()))
            .andExpect(jsonPath("$.birth").value(String.valueOf(response.getBirth())))
            .andExpect(jsonPath("$.gender").value(String.valueOf(response.getGender())))
            .andExpect(jsonPath("$.breed").value(response.getBreed()))
            .andExpect(jsonPath("$.isNeutered").value(response.isNeutered()))
            .andExpect(jsonPath("$.weight").value(response.getWeight()))
            .andExpect(jsonPath("$.description").value(response.getDescription()))
            .andExpect(jsonPath("$.imageUrl").value(response.getImageUrl()))
            .andDo(print())
            .andDo(document("pet/update-pet-profile/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("수정할 반려동물 프로필 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 수정 실패-반려동물 없을때")
    public void updatePetProfileFailure_PetNotFound() throws Exception {
        //given
        PetProfileUpdateRequest request = PetProfileUpdateRequest.builder()
            .weight(3.5f)
            .neutered(true)
            .description("귀여워요")
            .build();
        given(petService.updatePetProfile(anyLong(), any()))
            .willThrow(new PetNotFoundException());

        String requestString = objectMapper.writeValueAsString(request);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/mypage/pets/{petId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken")
                .content(requestString))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(PET_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(PET_NOT_FOUND.getMessage()))
            .andDo(print())
            .andDo(document("pet/update-pet-profile/failure/pet-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("수정할 반려동물 프로필 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 삭제 성공")
    public void deletePetProfile_Success() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        given(petService.deletePetProfile(anyString(), anyLong()))
            .willReturn(true);

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/mypage/pets/{petId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("pet/delete-pet-profile/success",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("삭제할 반려동물 프로필 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 삭제 실패 - 회원 못 찾았을 때")
    public void deletePetProfileFailure_MemberNotFound() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        given(petService.deletePetProfile(anyString(), anyLong()))
            .willThrow(new MemberNotFoundException());

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/mypage/pets/{petId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.code").value(MEMBER_NOT_FOUND.getCode()))
            .andExpect(jsonPath("$.message").value(MEMBER_NOT_FOUND.getMessage()))
            .andDo(document("pet/delete-pet-profile/failure/member-not-found",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("삭제할 반려동물 프로필 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 삭제 실패 - 반려동물이 1마리 일때")
    public void deletePetProfileFailure_MinExistPet() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        given(petService.deletePetProfile(anyString(), anyLong()))
            .willThrow(new FailedDeletePetException());

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/mypage/pets/{petId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.code").value(FAILED_DELETE_PET.getCode()))
            .andExpect(jsonPath("$.message").value(FAILED_DELETE_PET.getMessage()))
            .andDo(document("pet/delete-pet-profile/failure/minimum-one-more-pet",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("삭제할 반려동물 프로필 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("반려동물 프로필 삭제 실패 - 대표 반려동물일때")
    public void deletePetProfileFailure_RepresentativePet() throws Exception {
        //given
        String email = "test@test.com";
        Principal principal = Mockito.mock(Principal.class);
        given(principal.getName()).willReturn(email);

        given(petService.deletePetProfile(anyString(), anyLong()))
            .willThrow(new FailedDeleteRepresentativeException());

        //when
        //then
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/mypage/pets/{petId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .header("Authorization", "accessToken"))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andExpect(jsonPath("$.code").value(FAILED_DELETE_REPRESENTATIVE.getCode()))
            .andExpect(jsonPath("$.message").value(FAILED_DELETE_REPRESENTATIVE.getMessage()))
            .andDo(document("pet/delete-pet-profile/failure/failed-delete-representative",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(parameterWithName("petId").description("삭제할 반려동물 프로필 Id")),
                requestHeaders(
                    headerWithName("Authorization").description("Access Token"))
            ));
    }
}