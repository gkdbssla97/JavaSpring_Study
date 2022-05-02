package study.datajpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired MemberRepository memberRepository;
    @Test
    public void MemberTest() throws Exception {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("name1", 10, teamA);
        Member member2 = new Member("name2", 20, teamA);
        Member member3 = new Member("name3", 30, teamB);
        Member member4 = new Member("name4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();
        for (Member member : members) {
            System.out.println("member = "+ member);
            System.out.println("member.team = "+ member.getId());

        }
    }

    @Test
    public void TimeTest() throws Exception {
        Member member = new Member("member1");

        memberRepository.save(member); //PrePersist

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        System.out.println("findMember.c = " + findMember.getCreateDate());
        System.out.println("findMember.u = " + findMember.getLastModifiedDate());
        System.out.println("findMember.cb = " + findMember.getCreatedBy());
        System.out.println("findMember.ub = " + findMember.getLastModifiedBy());
    }

}