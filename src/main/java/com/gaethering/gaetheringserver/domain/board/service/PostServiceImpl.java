package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.aws.s3.S3Service;
import com.gaethering.gaetheringserver.domain.board.dto.PostRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Category;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.entity.PostImage;
import com.gaethering.gaetheringserver.domain.board.exception.CategoryNotFoundException;
import com.gaethering.gaetheringserver.domain.board.repository.CategoryRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostImageRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public PostResponse writePost(String email,
        List<MultipartFile> files, PostRequest request) {

        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberNotFoundException());

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new CategoryNotFoundException());

        Post post = Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .category(category)
            .member(member)
            .build();

        List<String> imgUrls = getImageUrlsInRequest(files);

        if (!imgUrls.isEmpty()) {
            for (String imgUrl : imgUrls) {
                PostImage image = PostImage.builder()
                    .imageUrl(imgUrl)
                    .isRepresentative(false)
                    .post(post)
                    .build();

                post.addImage(postImageRepository.save(image));
            }
        }

        postRepository.save(post);

        return PostResponse.builder()
            .categoryName(post.getCategory().getCategoryName())
            .title(post.getTitle())
            .content(post.getContent())
            .imageUrls(imgUrls)
            .viewCnt(0)
            .heartCnt(0)
            .createAt(post.getCreatedAt())
            .nickname(post.getMember().getNickname())
            .build();
    }

    @Override
    public List<String> getImageUrlsInRequest(List<MultipartFile> files) {
        List<String> imgUrls = new ArrayList<>();

        if (!CollectionUtils.isEmpty(files)) {
            for (MultipartFile file : files) {
                String imgUrl = s3Service.uploadImage(file);
                imgUrls.add(imgUrl);
            }
        }
        return imgUrls;
    }
}