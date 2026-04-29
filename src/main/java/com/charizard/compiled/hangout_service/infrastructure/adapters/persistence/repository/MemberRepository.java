package com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.repository;

import com.charizard.compiled.hangout_service.infrastructure.adapters.persistence.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {

    int countByParcheId(UUID parcheId);
}
