package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbItemParamDubboService;
import com.ego.mapper.TbItemParamMapper;
import com.ego.pojo.TbItemParam;
import com.ego.pojo.TbItemParamExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TbItemParamDubboServiceImpl implements TbItemParamDubboService {
    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    @Override
    public List<TbItemParam> selectByPage(int pageNumber, int pageSize) {
        //设置分页参数
        PageHelper.startPage(pageNumber,pageSize);
        //如果查询结果包含text类型列，一定使用 withBlobs
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(null);
        PageInfo<TbItemParam> pageInfo = new PageInfo<>(list);
        return pageInfo.getList();
    }

    @Override
    public long selectCount() {
        return tbItemParamMapper.countByExample(null);
    }

    @Override
    public TbItemParam selectByCatId(long id) {
        TbItemParamExample example = new TbItemParamExample();
        example.createCriteria().andItemCatIdEqualTo(id);
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(example);
        if(list != null && list.size() == 1) return list.get(0);
        return null;
    }

    @Override
    @Transactional
    public int insert(TbItemParam tbItemParam) {
        int index = tbItemParamMapper.insert(tbItemParam);
        if(index == 1){
            return 1;
        }
        throw new DaoException("新增商品规格模板失败");
    }

    @Override
    public int delete(long[] ids) throws DaoException {
        int index = 0;
        for(long id : ids){
            index += tbItemParamMapper.deleteByPrimaryKey(id);

        }
        if(index == ids.length) return 1;
        throw new DaoException("规格模板批量删除失败");
    }
}
