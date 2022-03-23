package com.ego.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PicService {
    /**
     * springmvc上传文件对象
     * @param file 文件
     * @return
     */
    Map<String,Object> update(MultipartFile file);
}
