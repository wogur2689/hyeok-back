package com.hyeok.back.member.service;

import com.hyeok.back.enums.ApiCode;
import com.hyeok.back.hyeokException.HyeokException;
import com.hyeok.back.member.entity.MemberEntity;
import com.hyeok.back.member.dto.Member;
import com.hyeok.back.member.param.SignUpReq;
import com.hyeok.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /* 비밀번호 암호화 */
    public String encryption(String password) {
        return passwordEncoder.encode(password);
    }

    /* 회원가입 */
    @Transactional
    public Boolean saveJoin(SignUpReq req) {
        Optional<MemberEntity> resultID = Optional.ofNullable(findByUserId(req.getUserId()));
        Optional<MemberEntity> resultNickName = Optional.ofNullable(findByNickName(req.getNickName()));
        if(resultID.isPresent() || resultNickName.isPresent()) {
            return false; //ID또는 닉네임 중복.
        }
        Member member = Member.builder()
                .userId(req.getUserId())
                .name(req.getName())
                .nickName(req.getNickName())
                .password(encryption(req.getPassword()))
                .address(req.getAddress())
                .phoneNumber(req.getPhoneNumber())
                .build();
        memberRepository.save(member.toEntity());
        return true;
    }

    /* 로그인 */
    @Transactional
    public Boolean selectJoin(String id, String pw) {
        Optional<MemberEntity> result = Optional.ofNullable(findByUserId(id));

        //id 조회
        if(result.isEmpty()) {
            throw new HyeokException(ApiCode.API_2000); //id 없음.
        }

        //pw 조회
        if(!passwordEncoder.matches(pw, result.get().getPassword())) {
             throw new HyeokException(ApiCode.API_2001); //비밀번호 불일치
        }

        return true; //로그인 성공
    }

    /* 입력한 ID로 조회 */
    @Transactional(readOnly = true)
    public MemberEntity findByUserId(String id) {
        return memberRepository.findByUserId(id);
    }

    /* 입력한 닉네임으로 조회 */
    @Transactional(readOnly = true)
    public MemberEntity findByNickName(String nickName) {
        return memberRepository.findByNickName(nickName);
    }
}
