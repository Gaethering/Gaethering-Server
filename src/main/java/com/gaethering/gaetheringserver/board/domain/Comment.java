package com.gaethering.gaetheringserver.board.domain;

import com.gaethering.gaetheringserver.core.domain.BaseTimeEntity;
import com.gaethering.gaetheringserver.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String comment;

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
}
