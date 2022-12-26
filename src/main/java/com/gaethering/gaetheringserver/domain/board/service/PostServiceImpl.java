package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.aws.s3.S3Service;
import com.gaethering.gaetheringserver.domain.board.dto.*;
import com.gaethering.gaetheringserver.domain.board.entity.Category;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.entity.PostImage;
import com.gaethering.gaetheringserver.domain.board.exception.CategoryNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionDeletePostException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionUpdatePostException;
import com.gaethering.gaetheringserver.domain.board.exception.PostImageNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.repository.CategoryRepository;
import com.gaethering.gaetheringserver.domain.board.repository.CommentRepository;
import com.gaethering.gaetheringserver.domain.board.repository.HeartRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostImageRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.board.util.ScrollPagingUtil;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final String DIR = "post";

    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final HeartRepository heartRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public PostWriteResponse writePost(String email, Long categoryId,
        List<MultipartFile> files, PostWriteRequest request) {

        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(CategoryNotFoundException::new);

        Post post = Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .category(category)
            .member(member)
            .postImages(new ArrayList<>())
            .build();

        postRepository.save(post);

        List<String> imgUrls = getImageUrlsInRequest(files);
        List<PostWriteImageUrlResponse> imageUrlResponses = new ArrayList<>();

        if (!imgUrls.isEmpty()) {
            boolean representative = true;
            for (String imgUrl : imgUrls) {
                PostImage image = PostImage.builder()
                    .imageUrl(imgUrl)
                    .isRepresentative(representative)
                    .post(post)
                    .build();

                post.addImage(postImageRepository.save(image));

                imageUrlResponses.add(PostWriteImageUrlResponse.builder()
                    .representative(representative)
                    .imageUrl(image.getImageUrl())
                    .build());

                representative = false;
            }
        }

        return PostWriteResponse.builder()
            .postId(post.getId())
            .categoryName(post.getCategory().getCategoryName())
            .title(post.getTitle())
            .content(post.getContent())
            .imageUrls(imageUrlResponses)
            .viewCnt(0)
            .heartCnt(0)
            .createdAt(post.getCreatedAt())
            .nickname(post.getMember().getNickname())
            .build();
    }

    @Override
    @Transactional
    public PostUpdateResponse updatePost(String email, Long postId, PostUpdateRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (!member.getId().equals(post.getMember().getId())) {
            throw new NoPermissionUpdatePostException();
        }

        Long heartCount = heartRepository.countByPost(post);

        post.updatePost(request.getTitle(), request.getContent());

        return PostUpdateResponse.from(post, member, heartCount.intValue());
    }

    @Override
    @Transactional
    public PostImageUploadResponse uploadPostImage(String email, Long postId, MultipartFile file) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (!member.getId().equals(post.getMember().getId())) {
            throw new NoPermissionUpdatePostException();
        }

        String imageUrl = s3Service.uploadImage(file, DIR);

        PostImage postImage = PostImage.builder()
                .imageUrl(imageUrl)
                .isRepresentative(false)
                .post(post)
                .build();

        PostImage savedPostImage = postImageRepository.save(postImage);

        return PostImageUploadResponse.builder()
                .imageId(savedPostImage.getId())
                .imageUrl(savedPostImage.getImageUrl())
                .representative(savedPostImage.isRepresentative())
                .createdAt(savedPostImage.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public boolean deletePostImage(String email, Long postId, Long imageId) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (!member.getId().equals(post.getMember().getId())) {
            throw new NoPermissionUpdatePostException();
        }

        PostImage postImage = postImageRepository.findById(imageId)
                .orElseThrow(PostImageNotFoundException::new);

        s3Service.removeImage(postImage.getImageUrl(), DIR);
        postImageRepository.delete(postImage);

        return true;
    }

    @Override
    @Transactional
    public boolean deletePost(String email, Long postId) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(MemberNotFoundException::new);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

        if (!member.getId().equals(post.getMember().getId())) {
            throw new NoPermissionDeletePostException();
        }
        List<PostImage> postImages = postImageRepository.findAllByPost(post);

        heartRepository.deleteHeartAllByPostId(post.getId());
        commentRepository.deleteCommentsAllByPostId(post.getId());

        if (!postImages.isEmpty()) {
            deletePostImages(postImages);
        }
        postRepository.delete(post);

        return true;
    }

    private void deletePostImages(List<PostImage> postImages) {
        for (PostImage postImage : postImages) {
            s3Service.removeImage(postImage.getImageUrl(), DIR);
        }
    }

    public List<String> getImageUrlsInRequest(List<MultipartFile> files) {
        List<String> imgUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String imgUrl = s3Service.uploadImage(file, DIR);
                imgUrls.add(imgUrl);
            }
        }

        return imgUrls;
    }

    @Override
    @Transactional
    public PostsGetResponse getPosts(String email, Long categoryId, int size, long lastPostId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        PageRequest pageRequest = PageRequest.of(0, size + 1);

        List<Post> posts
                = postRepository.findAllByCategoryAndIdIsLessThanOrderByIdDesc(category, lastPostId, pageRequest);

        List<PostDetailResponse> postResponses = new ArrayList<>();

        for(Post post : posts) {

            PostDetailResponse response = PostDetailResponse.fromEntity(post);

            response.setHasHeart(heartRepository.existsByPostAndMember(post, member));

            Optional<PostImage> optionalPostImage
                    = postImageRepository.findByPostAndIsRepresentativeIsTrue(post);

            if(optionalPostImage.isPresent()) {
                PostImage representativeImg = optionalPostImage.get();

                String imgUrl = representativeImg.getImageUrl();
                response.setImageUrl(imgUrl);
            }

            postResponses.add(response);
        }

        ScrollPagingUtil<PostDetailResponse> postsCursor = ScrollPagingUtil.of(postResponses, size);
        return PostsGetResponse.of(postsCursor, postRepository.countByCategory(category));
    }

    @Override
    @Transactional
    public PostGetOneResponse getOnePost(Long categoryId, String email, Long postId) {

        postRepository.updateViewCountByPostId(postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());

        if(!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException();
        }

        List<PostGetImageUrlResponse> imgUrls
                = postImageRepository.findAllByPost(post)
                .stream().map(PostGetImageUrlResponse:: fromEntity).collect(Collectors.toList());

        PostGetOneResponse response = PostGetOneResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .heartCnt(post.getHearts().size())
                .viewCnt(post.getViewCnt())
                .createdAt(post.getCreatedAt())
                .nickname(post.getMember().getNickname())
                .images(imgUrls)
                .build();

        response.setOwner(email.equals(post.getMember().getEmail()));
        return response;
    }
}