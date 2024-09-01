package com.aidiary.core.service;

import com.aidiary.core.entity.DiariesEntity;
import com.aidiary.core.repository.JpaDiariesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class DiariesDatabaseWriteService {

    private final JpaDiariesRepository jpaDiariesRepository;

    public DiariesEntity save(DiariesEntity diariesEntity) {
        return jpaDiariesRepository.save(diariesEntity);
    }

}
