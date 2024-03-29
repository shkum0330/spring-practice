package com.study.datajpa.repository;

import com.study.datajpa.dto.MemberDto;
import com.study.datajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long>,MemberRepositoryCustom {

    List<Member> findByUsername(String username);
    List<Member> findByUsernameAndAgeGreaterThan(String username,int age);
    @Query("select m from Member m where m.username= :username and m.age= :age")
    List<Member> findUser(@Param("username")String username, @Param("age") int age);
    @Query("select m.username from Member m")
    List<String> findUsernameList();
    @Query("select new com.study.datajpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);
    Page<Member> findByAge(int age, Pageable pageable);
    @Query(value = "select m from Member m", countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age=m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(String username);
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    <T> List<T> findProjectionsByUsername(String usernamee, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery =
            true)
    Member findByNativeQuery(String username);

    @Query(value = "SELECT m.member_id as id, m.username, t.name as teamName " +
            "FROM member m left join team t ON m.team_id = t.team_id",
            countQuery = "SELECT count(*) from member", nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
