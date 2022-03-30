package com.ego.commons.pojo;

/**
 * 商品详情页面
 * 建立此类的目的主要是对应数据库image属性，前端要的是images数组
 * 另外TbItem pojo类还有其他冗余属性
 */
public class TbItemDetails {
    private long id;
    private String title;
    private String sellPoint;
    private long price;
    private String[] images;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}
