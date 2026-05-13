package com.aibrief.mapper;

import com.aibrief.model.BriefingItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BriefingItemMapper {
    void insert(BriefingItem item);
    List<BriefingItem> findByBriefingId(@Param("briefingId") Long briefingId);
}
