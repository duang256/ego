package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.mapper.TbItemDescMapper;
import com.ego.mapper.TbItemMapper;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemDesc;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 表示当前类实现类接口发布到zookeeper中
 * consumer通过referencec可以进行远程注入
 * 如果consumer中使用了dubbo的这个service会报错20880端口错误
 */
@Service
public class TbItemDubboServiceImpl implements  TbItemDubboService{
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemDescMapper tbItemDescMapper;


    @Override
    public List<TbItem> selectByPage(int pageSize, int pageNumber) {

        //因为example只能做where限定，分页做不了，传入null即可
        // 另外使用pageHelper分页,分页插件必须写在查询的上面
        PageHelper.startPage(pageNumber,pageSize);
        List<TbItem> list = tbItemMapper.selectByExample(null);
        PageInfo<TbItem> pi  = new PageInfo<>(list);
        return pi.getList();
    }

    @Override
    public long selectCount() {
        return tbItemMapper.countByExample(null);
    }

    @Override
    //监听到异常，执行事务回滚
    @Transactional
    public int updateStatusByIds(long[] ids, int status) throws DaoException{
        int index = 0;
        for(long id : ids){
            TbItem tbItem = new TbItem();
            tbItem.setId(id);
            tbItem.setStatus((byte)status);
            tbItem.setUpdated(new Date());
            index += tbItemMapper.updateByPrimaryKeySelective(tbItem);
        }
        if(index == ids.length) return 1;
        throw new DaoException("批量修改失败");
    }

    @Override
    @Transactional
    public int insert(TbItem tbItem, TbItemDesc tbItemDesc) throws  DaoException{
        int index = tbItemMapper.insert(tbItem);
        if(index == 1) {
            int index2 = tbItemDescMapper.insert(tbItemDesc);
            if(index2 == 1){
                //只有商品新增和商品描述新增都成功才表示业务成功
                return 1;
            }

        }
        //否则表示新增失败
        throw new DaoException("新增商品失败");
    }

    @Override
    @Transactional
    public int update(TbItem tbItem, TbItemDesc tbItemDesc) throws DaoException {
        int index = tbItemMapper.updateByPrimaryKeySelective(tbItem);
        if(index == 1){
            int index2 = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);
            if(index2 == 1){
                return 1;
            }
        }
        throw  new DaoException("商品信息修改失败");
    }
}
