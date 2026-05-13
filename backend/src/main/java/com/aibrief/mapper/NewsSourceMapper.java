package com.aibrief.mapper;

import com.aibrief.model.NewsSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NewsSourceMapper {
    List<NewsSource> findAll();
    List<NewsSource> findByEnabledTrue();
    Optional<NewsSource> findById(@Param("id") Long id);
    void insert(NewsSource source);
    void update(NewsSource source);
    void deleteById(@Param("id") Long id);
}
