package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    @DisplayName("회원 가입")
    @Test
    @Rollback(value = false)
    public void 회원가입() throws Exception {
        Member member = new Member();
        member.setName("하윤");
        Long saveId = memberService.join(member);
        //em.flush();
        //em.clear();
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복회원조회() throws Exception {
        Member member = new Member();
        member.setName("람빈");
        Member member2 = new Member();
        member2.setName("람빈");

        memberService.join(member);
        memberService.join(member2);
        fail("실패");
    }
}