package com.ego.portal.service;

import com.ego.commons.pojo.BigAd;

public interface PortalService {
    //显示大广告位
    String showBigAd();
    //更新redis缓存，被rabbitmq receive 调用
    String bigAdUpdate();
}
