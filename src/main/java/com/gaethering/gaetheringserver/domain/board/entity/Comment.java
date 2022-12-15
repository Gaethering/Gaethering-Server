package com.gaethering.gaetheringserver.domain.board.entity;

import com.gaethering.gaetheringserver.core.entity.BaseTimeEntity;
import com.gaethering.gaetheringserver.domain.member.entity.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false, length = 800)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    public void setPost(Post post) {
        this.post = post;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setComment(String content) {
        this.content = content;
    }
}
