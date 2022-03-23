package com.ego.service.impl;

import com.ego.commons.pojo.EasyUITree;
import com.ego.dubbo.service.TbItemCatDubboService;
import com.ego.pojo.TbItemCat;
import com.ego.service.TbItemCatService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TbItemCatServiceImpl implements TbItemCatService {
    @Reference
    private TbItemCatDubboService tbItemCatDubboService;

    @Override
    public List<EasyUITree> showTree(long pid) {
        List<EasyUITree> listTree = new ArrayList<>();
        List<TbItemCat> list = tbItemCatDubboService.selectByPid(pid);
        for(TbItemCat cat : list){
            EasyUITree tree = new EasyUITree();
            tree.setId(cat.getId());
            tree.setText(cat.getName());
            tree.setState(cat.getIsParent() ? "closed":"open");
            listTree.add(tree);
        }
        return listTree;
    }
}
