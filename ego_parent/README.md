# Ego商城



![image-20220402211247688](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220402211247688.png)



## 模块介绍

- ego_api: provider接口
- ego_cart:购物车  **端口 8085**
- ego_commons:公用模块，用于软编码、工具类
- ego_item:商品详情  **端口8081**
- ego_manage:后台管理   **端口80**
- ego_mapper: 根据数据库表用mybatis generator 生成的
- ego_passport:登录注册    **端口8084**
- ego_pojo:同样，用mybatis generator 生成的
- ego_portal:门户 
- ego_provider: 担任dubbo中的provider角色，实现ego_api接口
- ego_rabbit_send: 发送rabbitmq消息，作为jar包被其他模块使用
- ego_redis_receive:接收rabbitmq消息，执行同步或异步消息  **端口8087**
- ego_redis:作为jar包供其他项目引入redis,配置了序列化器，要注意这个配置的是使用原生redisTemplate的
- ego_search:商品搜索 solr   **端口8083**
- ego_trade:订单   **端口8086**



## 环境搭建



![image-20220402220222031](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20220402220222031.png)

provider1和provider2配置相同的注册名到zk，自动集群化，nginx负载均衡portal1和portal2，fastdfs做文件存储，solr做商品搜索，mysql1、mysql2主从分离、mycat分库分表（限于电脑配置，这里将分片放在了一个主机上，理论上应该每个分片一个主从分离）、rabbitmq中间价同步异步消息、redis做商品、内容缓存，用redis做springSession



所有环境相关文件都一起传到了github中



### 虚拟机环境

最好是开始装一个有阿里源的、装JDK的、关闭虚拟机防火墙（重启生效的）纯净版用于克隆，因为需要的虚拟机比较多。阿里源最近更新了

https://blog.csdn.net/qq_41422009/article/details/122865240?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1.pc_relevant_default&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1.pc_relevant_default&utm_relevant_index=2



