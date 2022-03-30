package com.ego.item.service.impl;

import com.ego.commons.pojo.TbItemDetails;
import com.ego.commons.utils.JsonUtils;
import com.ego.dubbo.service.TbItemCatDubboService;
import com.ego.dubbo.service.TbItemDescDubboService;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.dubbo.service.TbItemParamItemDubboService;
import com.ego.item.pojo.CategoryNode;
import com.ego.item.pojo.ItemCategoryNav;
import com.ego.item.pojo.Param;
import com.ego.item.pojo.ParamItem;
import com.ego.item.service.ItemService;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemCat;
import com.ego.pojo.TbItemDesc;
import com.ego.pojo.TbItemParamItem;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Reference
    private TbItemCatDubboService tbItemCatDubboService;

    @Reference
    private TbItemDubboService tbItemDubboService;

    @Reference
    private TbItemDescDubboService tbItemDescDubboService;

    @Reference
    private TbItemParamItemDubboService tbItemParamItemDubboService;

    @Override
    @Cacheable(cacheNames = "com.ego.item",key = "'showItemCat'")
    public ItemCategoryNav showItemCat() {
        System.out.println("缓存导航菜单");
        ItemCategoryNav itemCategoryNav = new ItemCategoryNav();
        itemCategoryNav.setData(getAllItemCat(0L));
        return itemCategoryNav;
    }

    private List<Object> getAllItemCat(long parentId){
        //查询的子菜单
        List<TbItemCat> list = tbItemCatDubboService.selectByPid(parentId);
        //准备返回的List，将子菜单封装到List中并递归
        List<Object> listResult = new ArrayList<>();

        for(TbItemCat cat : list){
            //父节点递归
            if(cat.getIsParent()){
                CategoryNode node = new CategoryNode();
                node.setU("/products/" + cat.getId() + ".html");
                node.setN("<a href='/products/"+ cat.getId() + ".html'>"+ cat.getName() + "</a>" );
                node.setI(getAllItemCat(cat.getId()));
                listResult.add(node);
            }else{
                listResult.add("/products/" + cat.getId() + ".html|" + cat.getName());
            }
        }
        return listResult;

    }

    /**
     * 根据id缓存
     * @param id 商品id
     * @return
     */
    @Override
    @Cacheable(cacheNames = "com.ego.item",key = "'showItem:' + #id" )
    public TbItemDetails showItem(long id) {
        System.out.println(id + "缓存了");
        TbItem tbItem = tbItemDubboService.selectById(id);
        TbItemDetails details = new TbItemDetails();
        details.setId(id);
        details.setPrice(tbItem.getPrice());
        details.setSellPoint(tbItem.getSellPoint());
        details.setTitle(tbItem.getTitle());
        details.setImages(tbItem.getImage() != null && !tbItem.getImage().equals("") ? tbItem.getImage().split(",") : new String[1]);
        return details;
    }

    @Override
    public String showItemDesc(long id) {
        TbItemDesc tbItemDesc = tbItemDescDubboService.selectById(id);
        System.out.println("商品描述" + tbItemDesc.getItemDesc());
        return tbItemDesc.getItemDesc();
    }

    @Override
    public String showItemParam(long itemId) {
        TbItemParamItem tbItemParamItem = tbItemParamItemDubboService.selectByItemId(itemId);
        if(tbItemParamItem == null) return "";

        String paramData = tbItemParamItem.getParamData();
        List<ParamItem> list = JsonUtils.jsonToList(paramData, ParamItem.class);
        StringBuffer sf= new StringBuffer();
        for(ParamItem paramItem : list){
            sf.append("<table  style='color:gray;' width='100%'  cellpadding='5'>");
            for(int i = 0;i < paramItem.getParams().size();i++){
                sf.append("<tr>");
                if(i == 0){
                    //第一行显示分组信息
                    sf.append("<td style='width:150px;text-align:right;'>" + paramItem.getGroup() +"</td>");
                }else{
                    //其他行为空,此处必须给个空格，不然显示有问题
                    sf.append("<td> </td>");
                }
                sf.append("<td style='width:150px;text-align:right;'>" + paramItem.getParams().get(i).getK() +"</td>");
                sf.append("<td>" + paramItem.getParams().get(i).getV() +"</td>");
                sf.append("</tr>");
            }
            sf.append("</table>");
            sf.append("<hr style='color:gray;' />");
        }
        return sf.toString();
    }
}
