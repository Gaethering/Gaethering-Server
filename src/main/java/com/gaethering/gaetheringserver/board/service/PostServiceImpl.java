package com.gaethering.gaetheringserver.board.service;

import com.gaethering.gaetheringserver.board.domain.Post;
import com.gaethering.gaetheringserver.board.domain.PostImage;
import com.gaethering.gaetheringserver.board.dto.PostRequest;
import com.gaethering.gaetheringserver.board.dto.PostResponse;
import com.gaethering.gaetheringserver.board.repository.PostImageRepository;
import com.gaethering.gaetheringserver.board.repository.PostRepository;
import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.MemberNotFoundException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.util.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final ImageUploader imageUploader;
    private final MemberRepository memberRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public PostResponse writePost(String email,
                                  List<MultipartFile> files, PostRequest request) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .member(member)
                .build();

        List<String> imgUrlList = getImageUrlInRequest(files);

        if (!imgUrlList.isEmpty()) {
            for (String imgUrl : imgUrlList) {
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
                .category(post.getCategory())
                .title(post.getTitle())
                .content(post.getContent())
                .imgUrlList(imgUrlList)
                .viewCnt(0)
                .heartCnt(0)
                .createAt(post.getCreatedAt())
                .nickname(post.getMember().getNickname())
                .build();
    }

    @Override
    public List<String> getImageUrlInRequest(List<MultipartFile> files) {
        List<String> imgUrlList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(files)) {
            for (MultipartFile file : files) {
                String imgUrl = imageUploader.uploadImage(file);
                imgUrlList.add(imgUrl);
            }
        }
        return imgUrlList;
    }
}
