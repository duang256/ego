package com.ego.search.service.impl;

import com.ego.dubbo.service.TbItemCatDubboService;
import com.ego.dubbo.service.TbItemDescDubboService;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemCat;
import com.ego.pojo.TbItemDesc;
import com.ego.search.pojo.SearchPojo;
import com.ego.search.service.SearchService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrOperations solrOperations;

    @Reference
    private TbItemDubboService tbItemDubboService;

    @Reference
    private TbItemCatDubboService tbItemCatDubboService;

    @Reference
    private TbItemDescDubboService tbItemDescDubboService;

    @Override
    public Map<String, Object> search(String q,int page,int size) {
        HighlightQuery query = new SimpleHighlightQuery();

        //查询条件
        Criteria c = new Criteria("item_keywords");
        c.is(q);
        query.addCriteria(c);
        //排序规则
        query.addSort(Sort.by(Sort.Direction.DESC,"_version_"));

        //设置分页条件
        query.setOffset((long)size * (page - 1));
        query.setRows(size);

        //设置高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.setSimplePrefix("<span style='color:red'>");
        highlightOptions.setSimplePostfix("</span>");
        highlightOptions.addField("item_title");
        query.setHighlightOptions(highlightOptions);


        HighlightPage<SearchPojo> hlPage = solrOperations.queryForHighlightPage("ego", query, SearchPojo.class);
        List<HighlightEntry<SearchPojo>> highlighted = hlPage.getHighlighted();

        //创建的返回集合
        List<SearchPojo> listResult = new ArrayList<>();


        for(HighlightEntry<SearchPojo> hlEntity: highlighted){
            //非高亮数据
            SearchPojo searchPojo = hlEntity.getEntity();
            //把从solr中取出的image转换为images
            String image = searchPojo.getImage();
            searchPojo.setImages(image != null && !image.equals("") ? image.split(",") : new String[1]);

            //高亮数据
            List<HighlightEntry.Highlight> highlights = hlEntity.getHighlights();
            for(HighlightEntry.Highlight highlight : highlights){
                //当前这个对象item_title包含高亮数据
                if(highlight.getField().getName().equals("item_title")){
                    //将item_title替换为高亮
                    searchPojo.setTitle(highlight.getSnipplets().get(0));
                }
            }
            listResult.add(searchPojo);

        }


        Map<String,Object> map = new HashMap<>();
        map.put("itemList",listResult);
        map.put("query",q);
        map.put("totalPages",hlPage.getTotalPages());
        map.put("page",page);
        return map;
    }

    @Override
    public int insert(long[] ids) {
        List<SearchPojo> list = new ArrayList<>();
        for(long id : ids){
            SearchPojo sp = new SearchPojo();
            //赋值sp的属性，这些属性是通过id来查询的
            TbItem tbItem = tbItemDubboService.selectById(id);
            sp.setId(id);
            sp.setImage(tbItem.getImage());
            sp.setTitle(tbItem.getTitle());
            sp.setSellPoint(tbItem.getSellPoint());
            sp.setPrice(tbItem.getPrice());
            TbItemCat tbItemCat = tbItemCatDubboService.selectById(tbItem.getCid());
            sp.setCatName(tbItemCat.getName());
            TbItemDesc tbItemDesc = tbItemDescDubboService.selectById(id);
            sp.setDesc(tbItemDesc.getItemDesc());
            list.add(sp);
        }

        UpdateResponse response = solrOperations.saveBeans("ego", list);
        solrOperations.commit("ego");
        //新增成功状态值为0
        if(response.getStatus() ==  0){
            return 1;
        }
        return 0;
    }

    @Override
    public int delete(String[] ids) {
        List<String> list = Arrays.asList(ids);
        UpdateResponse response = solrOperations.deleteByIds("ego", list);
        solrOperations.commit("ego");
        //成功状态值为0
        if(response.getStatus() ==  0){
            return 1;
        }
        return 0;
    }
}
