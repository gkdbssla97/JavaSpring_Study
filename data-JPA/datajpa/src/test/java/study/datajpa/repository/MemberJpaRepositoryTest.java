package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberRepository;

    @Test
    public void testMember() throws Exception {
        Member member = new Member("Hayoon", 20);
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.find(savedMember.getId());
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("m1", 20);
        Member member2 = new Member("m2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        Member fmember1 = memberRepository.findById(member1.getId()).get();
        Member fmember2 = memberRepository.findById(member2.getId()).get();

        assertThat(fmember1.getId()).isEqualTo(member1.getId());
        assertThat(fmember2.getId()).isEqualTo(member2.getId());
        assertThat(fmember1).isEqualTo(member1);
        assertThat(fmember2).isEqualTo(member2);

        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long delcnt = memberRepository.count();
        assertThat(delcnt).isEqualTo(0);
    }
    @Test
    public void AgeTest() throws Exception {
        Member member = new Member("AAA", 20);
        Member member3 = new Member("BBB", 30);
        memberRepository.save(member);
        memberRepository.save(member3);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThen("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }
    @Test
    public void paging() throws Exception {

        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 10));
        memberRepository.save(new Member("m3", 10));
        memberRepository.save(new Member("m4", 10));
        memberRepository.save(new Member("m5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> byPage = memberRepository.findByPage(age, offset, limit);
        long l = memberRepository.totalCount(age);

        assertThat(byPage.size()).isEqualTo(3);
        assertThat(l).isEqualTo(5);
    }
    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 10));
        memberRepository.save(new Member("m3", 10));
        memberRepository.save(new Member("m4", 10));

        int i = memberRepository.bulkAgePlus(10);
        assertThat(i).isEqualTo(4);
    }
}