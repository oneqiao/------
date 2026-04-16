package com.example.cardmanagement.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页工具类
 * 用于处理API接口的分页查询
 */
public class PageUtil {
    
    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 1;
    
    /**
     * 默认每页条数
     */
    public static final int DEFAULT_SIZE = 10;
    
    /**
     * 最大每页条数
     */
    public static final int MAX_SIZE = 100;
    
    /**
     * 创建分页请求对象
     * @param page 页码
     * @param size 每页条数
     * @return Pageable 分页请求对象
     */
    public static Pageable createPageable(int page, int size) {
        // 处理页码，确保页码从1开始
        if (page < 1) {
            page = DEFAULT_PAGE;
        }
        
        // 处理每页条数，确保不超过最大值
        if (size < 1) {
            size = DEFAULT_SIZE;
        } else if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }
        
        // 创建分页请求，默认按ID降序排序
        return PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
    }
    
    /**
     * 构建分页响应数据
     * @param page 分页对象
     * @return Map 分页响应数据
     */
    public static <T> Map<String, Object> buildPageResponse(Page<T> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("total", page.getTotalElements());
        response.put("page", page.getNumber() + 1);
        response.put("size", page.getSize());
        response.put("list", page.getContent());
        return response;
    }
}