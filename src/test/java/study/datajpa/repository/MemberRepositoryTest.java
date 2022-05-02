package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.text.html.parser.Entity;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void memberTest() throws Exception {
        Member member = new Member("memA", 20);
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCURD_2() throws Exception {
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
    public void findByUsernameAndAgeGreaterThan() throws Exception {

        Member member = new Member("AAA", 20);
        Member member3 = new Member("BBB", 30);
        memberRepository.save(member);
        memberRepository.save(member3);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
        List<Member> all = memberRepository.findAll();
    }

    @Test
    public void testQuery() throws Exception {
        Member member = new Member("AAA", 10);
        Member member3 = new Member("BBB", 30);
        memberRepository.save(member);
        memberRepository.save(member3);

        List<Member> aaa = memberRepository.findUser("AAA", 10);
        assertThat(aaa.get(0)).isEqualTo(member);
    }

    @Test
    public void findUsernameList() throws Exception {
        Member member = new Member("AAA", 10);
        Member member3 = new Member("BBB", 30);
        memberRepository.save(member);
        memberRepository.save(member3);

        List<String> usernameS = memberRepository.findUsername();
        for (String username : usernameS) {
            System.out.println("s = " + username);
        }
    }

    @Test
    public void findMemberDto() throws Exception {
        Team team = new Team("TeamA");
        teamRepository.save(team);

        Member member = new Member("AAA", 20);
        Member member3 = new Member("BBB", 30);
        member.setTeam(team);
        member3.setTeam(team);

        memberRepository.save(member);
        memberRepository.save(member3);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("s = " + dto);
        }
    }

    @Test
    public void findByNames() throws Exception {
        Member member = new Member("AAA", 20);
        Member member3 = new Member("BBB", 30);

        memberRepository.save(member);
        memberRepository.save(member3);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member byName : byNames) {
            System.out.println("s = " + byName);
        }
    }

    @Test
    public void paging() throws Exception {

        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 10));
        memberRepository.save(new Member("m3", 10));
        memberRepository.save(new Member("m4", 10));
        memberRepository.save(new Member("m5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();
        for (Member member : content) {
            System.out.println("Member = " + member);
        }
        System.out.println("totalElem = " + totalElements);
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }
    @Test
    public void bulkUpdate() {
        memberRepository.save(new Member("m1", 10));
        memberRepository.save(new Member("m2", 10));
        memberRepository.save(new Member("m3", 10));
        memberRepository.save(new Member("m4", 10));

        int i = memberRepository.bulkAgePlus(10);

        List<Member> result = memberRepository.findByUsername("m4");
        Member member = result.get(0);
        System.out.println("m4 = " + member);

        assertThat(i).isEqualTo(4);
    }
    @Test
    public void findMemberLazy() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("m1", 10, teamA);
        Member member2 = new Member("m1", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();
        List<Member> all = memberRepository.findEntityGraphByUsername("m1");
        for (Member member : all) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member = " + member.getTeam().getName());
        }
    }
    @Test
    public void queryHint() {
        Member member = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member.getId()).get();
        findMember.setUsername("member2");

        em.flush();
    }
    
    @Test
    public void callCustom() throws Exception {
        List<Member> findMember = memberRepository.findMemberCustom();
    }
}