package com.ego.portal.service.impl;

import com.ego.commons.pojo.BigAd;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.pojo.TbContent;
import com.ego.portal.service.PortalService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PortalServiceImpl implements PortalService {
    @Reference
    private TbContentDubboService tbContentDubboService;
    @Value("${ego.bigad.categoryId}")
    private Long categoryId;
    
    @Override
    @Cacheable(cacheNames = "com.ego.portal",key = "'bigad'")
    public String showBigAd() {
        List<TbContent> list = tbContentDubboService.selectAllByCategoryIdOrder(categoryId);
        List<BigAd> adList = new ArrayList<>();

        for(TbContent tbContent: list){
            BigAd bigAd = new BigAd();
            bigAd.setSrc(tbContent.getPic());
            bigAd.setAlt("");
            bigAd.setHeight(240);
            bigAd.setHeightB(240);
            bigAd.setSrcB(tbContent.getPic2());
            bigAd.setWidth(670);
            bigAd.setWidthB(550);
            bigAd.setHref(tbContent.getUrl());
            adList.add(bigAd);
        }
        //list数据转换为JSON字符串，Jackson转换
        return JsonUtils.objectToJson(adList);
    }


}
