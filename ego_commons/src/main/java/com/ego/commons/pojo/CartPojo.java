package com.ego.commons.pojo;

/**
 * 购物车实体类
 * 为前端需要数据的封装
 * 其中num为商品购物车数量，而非数据库中的库存数量
 */
public class CartPojo {
    private long id;
    private String title;
    private long price;
    private String[] images;
    private Integer num;

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

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}