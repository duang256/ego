package com.ego.trade.service.impl;

import com.ego.commons.pojo.DeleteCartPojo;
import com.ego.commons.pojo.OrderPojo;
import com.ego.commons.utils.ServletUtil;
import com.ego.pojo.TbOrderItem;
import com.ego.pojo.TbUser;
import com.ego.sender.Send;
import com.ego.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TradeServiceImpl implements TradeService {
    @Value("${ego.rabbitmq.order.createOrder}")
    private String createOrder;
    @Autowired
    private Send send;

    @Value("${ego.rabbitmq.order.deleteCart}")
    private String deleteCart;

    @Value("${ego.rabbitmq.mail}")
    private String mail;


    @Override
    public Map<String, Object> createOrder(OrderPojo orderPojo) {
        //此处为同步消息
        String res = (String)send.sendAndReceive(createOrder, orderPojo);

        Map<String,Object> resultMap = null;

        // 删除购物车对应商品 和 发送邮件功能均为rabbitmq异步操作  且都是在订单成功的基础上进行的


        if(res != null){

            //设定返回值
            resultMap = new HashMap<>();
            resultMap.put("orderId",res);
            resultMap.put("payment",orderPojo.getPayment());
            //此处简单设定为送货时长   当天11点前下单当天下午送到     11点到23点之前下单，预计第二天上午送到    23点之后子二天下午送到
            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour < 11){
                calendar.set(Calendar.HOUR_OF_DAY,17);
            }else if(hour >= 11 && hour < 23){
                calendar.add(Calendar.DATE,1);
                calendar.set(Calendar.HOUR_OF_DAY,9);
            }else{
                calendar.add(Calendar.DATE,1);
                calendar.set(Calendar.HOUR_OF_DAY,17);
            }
            resultMap.put("date",calendar.getTime());
            //返回值设置结束




            /*
            删除购物车的操作写在购物车模块里面，在此处进行rabbitmq调用
             */
            DeleteCartPojo dcp = new DeleteCartPojo();
            TbUser user = (TbUser)ServletUtil.getRequest().getSession().getAttribute("loginUser");
            dcp.setUserId(user.getId());

            List<TbOrderItem> list = orderPojo.getOrderItems();
            StringBuffer sf = new StringBuffer();
            for(int i = 0;i < list.size();i++){
                sf.append(list.get(i).getItemId());
                if(i < list.size() - 1){
                    sf.append(",");
                }
            }
            dcp.setItemIds(sf.toString());
            send.send(deleteCart,dcp);

            //发送邮件
            send.send(mail,res);

        }
        return resultMap;

    }
}
