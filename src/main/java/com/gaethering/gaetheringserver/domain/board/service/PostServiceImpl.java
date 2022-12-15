package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.aws.s3.S3Service;
import com.gaethering.gaetheringserver.domain.board.dto.PostImageUploadResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostResponse;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateRequest;
import com.gaethering.gaetheringserver.domain.board.dto.PostUpdateResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Category;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.entity.PostImage;
import com.gaethering.gaetheringserver.domain.board.exception.CategoryNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.NoPermissionUpdatePostException;
import com.gaethering.gaetheringserver.domain.board.exception.PostImageNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.repository.CategoryRepository;
import com.gaethering.gaetheringserver.domain.board.repository.HeartRepository;
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
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

	private final S3Service s3Service;
	private final MemberRepository memberRepository;
	private final PostImageRepository postImageRepository;
	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final HeartRepository heartRepository;

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
			.postImages(new ArrayList<>())
			.build();

		postRepository.save(post);

		List<String> imgUrls = getImageUrlsInRequest(files);

		if (!imgUrls.isEmpty()) {
			boolean representative = true;
			for (String imgUrl : imgUrls) {
				PostImage image = PostImage.builder()
					.imageUrl(imgUrl)
					.isRepresentative(representative)
					.post(post)
					.build();

				post.addImage(postImageRepository.save(image));
				representative = false;
			}
		}

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

		String imageUrl = s3Service.uploadImage(file);

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

		s3Service.removeImage(postImage.getImageUrl());
		postImageRepository.delete(postImage);

		return true;
	}

	public List<String> getImageUrlsInRequest(List<MultipartFile> files) {
		List<String> imgUrls = new ArrayList<>();

		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				String imgUrl = s3Service.uploadImage(file);
				imgUrls.add(imgUrl);
			}
		}

		return imgUrls;
	}
}