package com.aibrief.mapper;

import com.aibrief.model.Briefing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface BriefingMapper {
    List<Briefing> findAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    Optional<Briefing> findById(@Param("id") Long id);
    Optional<Briefing> findByDate(@Param("date") LocalDate date);
    Optional<Briefing> findByDateWithItems(@Param("date") LocalDate date);
    void insert(Briefing briefing);
    void update(Briefing briefing);
}
