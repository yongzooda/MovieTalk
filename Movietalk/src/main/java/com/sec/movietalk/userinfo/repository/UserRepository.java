package com.sec.movietalk.userinfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sec.movietalk.common.domain.user.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email); // 중복 체크용
}
