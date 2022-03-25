package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbContentDubboService;
import com.ego.mapper.TbContentMapper;
import com.ego.pojo.TbContent;
import com.ego.pojo.TbContentExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TbContentDubboServiceImpl implements TbContentDubboService {
    @Autowired
    private TbContentMapper tbContentMapper;

    /**
     * 需要编写动态sql
     * 如果category=0就查询全部
     * @param categoryId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public List<TbContent> selectByPage(long categoryId, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber,pageSize);
        TbContentExample tbContentExample = new TbContentExample();
        if(categoryId != 0) {
            tbContentExample.createCriteria().andCategoryIdEqualTo(categoryId);
        }
        List<TbContent> list = tbContentMapper.selectByExampleWithBLOBs(tbContentExample);
        PageInfo<TbContent> pi = new PageInfo<>(list);
        return  pi.getList();
    }

    @Override
    public long selectCountByCategoryId(long categoryId) {
        TbContentExample tbContentExample = new TbContentExample();
        if(categoryId != 0) {
            tbContentExample.createCriteria().andCategoryIdEqualTo(categoryId);
        }
        return tbContentMapper.countByExample(tbContentExample);
    }

    @Override
    public int insert(TbContent tbContent) {
        return tbContentMapper.insert(tbContent);
    }

    @Override
    public int update(TbContent tbContent) {
        return tbContentMapper.updateByPrimaryKeySelective(tbContent);
    }

    @Override
    @Transactional
    public int delete(long[] ids) throws DaoException {
        int index = 0;
        for(long id : ids){
            index += tbContentMapper.deleteByPrimaryKey(id);
        }
        if(index == ids.length){
            return 1;
        }
        throw new DaoException("批量删除内容失败");
    }
}
