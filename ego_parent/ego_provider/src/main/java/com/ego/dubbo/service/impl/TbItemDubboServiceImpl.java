package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbItemDubboService;
import com.ego.mapper.TbItemDescMapper;
import com.ego.mapper.TbItemMapper;
import com.ego.mapper.TbItemParamItemMapper;
import com.ego.pojo.TbItem;
import com.ego.pojo.TbItemDesc;
import com.ego.pojo.TbItemParamItem;
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

    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;


    @Override
    public List<TbItem> selectByPage(int pageSize, int pageNumber) {
        //查询所有状态的商品
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
    public int insert(TbItem tbItem, TbItemDesc tbItemDesc, TbItemParamItem tbItemParamItem) throws  DaoException{
        int index = tbItemMapper.insert(tbItem);
        if(index == 1) {
            int index2 = tbItemDescMapper.insert(tbItemDesc);
            if(index2 == 1){
                //只有商品新增和商品描述新增都成功才表示业务成功
                int index3 = tbItemParamItemMapper.insert(tbItemParamItem);
                if(index3 == 1){
                    return 1;
                }
            }

        }
        //否则表示新增失败
        throw new DaoException("新增商品失败");
    }

    @Override
    @Transactional
    public int update(TbItem tbItem, TbItemDesc tbItemDesc,TbItemParamItem tbItemParamItem) throws DaoException {
        int index = tbItemMapper.updateByPrimaryKeySelective(tbItem);
        if(index == 1){
            int index2 = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);
            if(index2 == 1){
                int index3 = tbItemParamItemMapper.updateByPrimaryKeySelective(tbItemParamItem);
                if(index3 == 1) {
                    return 1;
                }
            }
        }
        throw  new DaoException("商品信息修改失败");
    }

    @Override
    public TbItem selectById(long id) {
        return tbItemMapper.selectByPrimaryKey(id);
    }
}
