package com.ego.service.impl;

import com.ego.commons.utils.FastDFSClient;
import com.ego.service.PicService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PicServiceImpl implements PicService {

    @Value("${ego.fastdfs.nginx}")
    private String nginxHost;
    @Override
    public Map<String, Object> update(MultipartFile file) {
        Map<String,Object> map = new HashMap<>();
        try {
            /*
            * file.getInputStream获取图片流
            * file.getOriginalFilename()获取图片名称
            * FastDFS图片会被FastDFS重新命名，第二个参数的意义是知道文件的扩展名
            * 返回值是FastDFS中存储的名字
            * */
            String[] result = FastDFSClient.uploadFile(file.getInputStream(), file.getOriginalFilename());
            map.put("error",0);
            map.put("url",nginxHost + result[0] + "/" + result[1]);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.put("error",1);
        map.put("message","错误信息");

        return map;
    }
}

