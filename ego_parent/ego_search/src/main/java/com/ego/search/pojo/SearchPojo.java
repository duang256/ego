package com.ego.search.pojo;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 并不是只是为了显示的实体类，该实体类需要与solr中的配置对应
 * Filed注解加映射solr中属性值
 */
public class SearchPojo {
    @Field("id")
    private long id;
    private String[] images;
    @Field("item_image")
    private String image;
    @Field("item_sell_point")
    private String sellPoint;
    @Field("item_price")
    private long price;
    @Field("item_title")
    private String title;
    @Field("item_desc")
    private String desc;
    @Field("item_category_name")
    private String catName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSellPoint() {
        return sellPoint;
    }

    public void setSellPoint(String sellPoint) {
        this.sellPoint = sellPoint;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
