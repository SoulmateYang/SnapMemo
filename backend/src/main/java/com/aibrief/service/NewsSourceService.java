package com.aibrief.service;

import com.aibrief.mapper.NewsSourceMapper;
import com.aibrief.model.NewsSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsSourceService {

    private final NewsSourceMapper newsSourceMapper;

    public NewsSourceService(NewsSourceMapper newsSourceMapper) {
        this.newsSourceMapper = newsSourceMapper;
    }

    public List<NewsSource> findAll() {
        return newsSourceMapper.findAll();
    }

    public NewsSource create(NewsSource source) {
        newsSourceMapper.insert(source);
        return source;
    }

    public Optional<NewsSource> update(Long id, NewsSource source) {
        return newsSourceMapper.findById(id).map(existing -> {
            source.setId(id);
            newsSourceMapper.update(source);
            return source;
        });
    }

    public boolean delete(Long id) {
        return newsSourceMapper.findById(id).map(existing -> {
            newsSourceMapper.deleteById(id);
            return true;
        }).orElse(false);
    }
}
