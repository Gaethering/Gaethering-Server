package com.gaethering.gaetheringserver.domain.board.service;

import com.gaethering.gaetheringserver.domain.board.dto.HeartResponse;
import com.gaethering.gaetheringserver.domain.board.entity.Heart;
import com.gaethering.gaetheringserver.domain.board.entity.Post;
import com.gaethering.gaetheringserver.domain.board.exception.AlreadyPushHeartException;
import com.gaethering.gaetheringserver.domain.board.exception.HeartNotFoundException;
import com.gaethering.gaetheringserver.domain.board.exception.PostNotFoundException;
import com.gaethering.gaetheringserver.domain.board.repository.HeartRepository;
import com.gaethering.gaetheringserver.domain.board.repository.PostRepository;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.member.MemberNotFoundException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HeartServiceImpl implements HeartService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;

    @Override
    public HeartResponse pushHeart(Long postId, String email) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        if (heartRepository.existsByPostAndMember(post, member)) {
            throw new AlreadyPushHeartException();
        }

        Heart heart = Heart.builder()
                .post(post)
                .member(member)
                .build();

        heartRepository.save(heart);

        heart.mappingPost(post);

        return HeartResponse.builder()
                .memberId(member.getId())
                .postId(post.getId())
                .likeCnt(heartRepository.countByPost(post).intValue())
                .build();
    }

    @Override
    public HeartResponse cancelHeart(Long postId, String email) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException());

        Heart heart = heartRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new HeartNotFoundException());

        heartRepository.delete(heart);
        post.cancelPostHeart(heart);

        return HeartResponse.builder()
                .memberId(member.getId())
                .postId(post.getId())
                .likeCnt(heartRepository.countByPost(post).intValue())
                .build();
    }
}
