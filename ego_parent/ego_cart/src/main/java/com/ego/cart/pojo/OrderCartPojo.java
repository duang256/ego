package com.ego.cart.pojo;

import com.ego.commons.pojo.CartPojo;

public class OrderCartPojo extends CartPojo {
    boolean enough;

    public boolean isEnough() {
        return enough;
    }

    public void setEnough(boolean enough) {
        this.enough = enough;
    }
}
