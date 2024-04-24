package com.example.tes24.service;

import com.example.tes24.entity.Member;
import com.example.tes24.repository.MemberRepository;
import com.example.tes24.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final JwtUtils jwtUtils;
    private final MemberRepository memberRepository;

    @Override
    public void delete(Member member) {
        memberRepository.delete(member);
    }

    @Override
    public Member updateAccessToken(Long memberId, String token) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        member.setAccessToken(token);
        return memberRepository.save(member);
    }

    @Override
    public Member updateRefreshToken(Long memberId, String token) {
        Member member = memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
        member.setRefreshToken(token);
        return memberRepository.save(member);
    }

    @Override
    public Member signUp(Member member) {
        member.setAccessToken(jwtUtils.createAccessToken(member.getMemberId()));
        member.setRefreshToken(jwtUtils.createRefreshToken(member.getMemberId()));
        return memberRepository.save(member);
    }

    @Override
    public Member login(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Member login(String memberId) {
        return memberRepository.findById(Long.valueOf(memberId)).orElseThrow(NoSuchElementException::new);
    }
}
