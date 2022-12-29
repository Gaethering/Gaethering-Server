package com.gaethering.gaetheringserver.domain.member.service.auth;

import com.gaethering.gaetheringserver.domain.member.entity.Member;
import com.gaethering.gaetheringserver.domain.member.exception.auth.DormantUserException;
import com.gaethering.gaetheringserver.domain.member.exception.auth.InActiveUserException;
import com.gaethering.gaetheringserver.domain.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.domain.member.type.MemberStatus;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String message = "사용자를 찾을 수 없습니다.";
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(message));

        if (MemberStatus.INACTIVE == member.getStatus()) {
            throw new InActiveUserException();
        } else if (MemberStatus.DORMANT == member.getStatus()) {
            throw new DormantUserException();
        }

        return new User(member.getEmail(), member.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));
    }
}
