package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
