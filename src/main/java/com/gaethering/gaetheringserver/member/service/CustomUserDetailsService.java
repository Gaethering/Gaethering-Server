package com.gaethering.gaetheringserver.member.service;

import com.gaethering.gaetheringserver.member.domain.Member;
import com.gaethering.gaetheringserver.member.exception.member.auth.DormantUserException;
import com.gaethering.gaetheringserver.member.exception.member.auth.InActiveUserException;
import com.gaethering.gaetheringserver.member.repository.member.MemberRepository;
import com.gaethering.gaetheringserver.member.type.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if(MemberStatus.INACTIVE == member.getStatus()) {
            throw new InActiveUserException();
        } else if (MemberStatus.DORMANT == member.getStatus()) {
            throw new DormantUserException();
        }

        return new User(member.getEmail(), member.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())));
    }
}
