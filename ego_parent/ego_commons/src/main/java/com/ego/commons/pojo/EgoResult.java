package com.ego.commons.pojo;

public class EgoResult {
    //页面200 成功 400、500失败
    private int status;
    //服务端向客户端发送的数据
    private Object data;
    //服务端向客户端发送的消息
    private String msg;

    public static EgoResult ok(){
        EgoResult er = new EgoResult();
        er.setStatus(200);
        er.setMsg("ok");
        return er;
    }

    public static EgoResult ok(Object data){
        EgoResult er = new EgoResult();
        er.setStatus(200);
        er.setData(data);
        er.setMsg("ok");
        return er;
    }

    public static EgoResult ok(String msg){
        EgoResult er = new EgoResult();
        er.setStatus(200);
        er.setMsg(msg);
        return er;
    }

    public static EgoResult err(String msg){
        EgoResult er = new EgoResult();
        er.setStatus(400);
        er.setMsg(msg);
        return er;
    }



    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
