package com.aidiary.user.domain.repository;

import com.aidiary.user.domain.entity.DiariesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDiariesRepository extends JpaRepository<DiariesEntity, Long> {


}
