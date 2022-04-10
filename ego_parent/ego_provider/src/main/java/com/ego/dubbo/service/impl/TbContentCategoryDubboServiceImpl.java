package com.ego.dubbo.service.impl;

import com.ego.commons.exception.DaoException;
import com.ego.dubbo.service.TbContentCategoryDubboService;
import com.ego.mapper.TbContentCategoryMapper;
import com.ego.pojo.TbContentCategory;
import com.ego.pojo.TbContentCategoryExample;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TbContentCategoryDubboServiceImpl implements TbContentCategoryDubboService {
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;

    @Override
    public List<TbContentCategory> selectByPid(long pid) {
        TbContentCategoryExample example = new TbContentCategoryExample();
        //不查询被删除内容
        example.createCriteria().andStatusEqualTo(1).andParentIdEqualTo(pid);
        //对查询排序，升序排序
        example.setOrderByClause("sort_order asc");
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        if(list.size() != 0) return list;
        return null;
    }

    @Override
    @Transactional
    public int insert(TbContentCategory tbContentCategory) {
        //判断当前名称是否重复
        TbContentCategoryExample example = new TbContentCategoryExample();
        example.createCriteria().andNameEqualTo(tbContentCategory.getName()).andStatusEqualTo(1);
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        if(list != null && list.size() == 0){
            int index = tbContentCategoryMapper.insert(tbContentCategory);
            if(index == 1){
                //判断父类目是否为true
                TbContentCategory parent = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
                if(!parent.getIsParent()){
                    TbContentCategory parUpdate = new TbContentCategory();
                    parUpdate.setId(parent.getId());
                    parUpdate.setIsParent(true);
                    int indexParent = tbContentCategoryMapper.updateByPrimaryKeySelective(parUpdate);
                    if(indexParent != 1){
                        throw new DaoException("新增类目-修改父节点失败");
                    }
                }
                return 1;
            }
        }
        throw new DaoException("新增类目-修改父节点失败");
    }

    /**
     * 如果有多条DML，需要进行事务控制，并且抛出异常
     * 如果只有一条DML，那么不出现异常就可以
     * @param tbContentCategory
     * @return
     */
    @Override
    public int updateNameById(TbContentCategory tbContentCategory) {
        //判断当前名称是否重复
        TbContentCategoryExample example = new TbContentCategoryExample();
        example.createCriteria().andNameEqualTo(tbContentCategory.getName()).andStatusEqualTo(1);
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        if(list != null && list.size() == 0){
            int index = tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
            return index;
        }
        return 0;
    }

    /**
     * 逻辑状态删除内容分类
     * @param id
     * @return
     * @throws DaoException
     */
    @Override
    @Transactional
    public int deleteById(long id) throws DaoException{
        Date date = new Date();

        TbContentCategory tbContentCategory = new TbContentCategory();
        tbContentCategory.setId(id);
        tbContentCategory.setUpdated(date);
        tbContentCategory.setStatus(2);
        //删除当前节点
        int index = tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
        if(index == 1){

            deleteChildrenById(id,date);

            //判断当前节点的父节点是否还有其他正常状态的子节点，如果没有，则父节点is_parent应改为false

            //当前节点
            TbContentCategory current = tbContentCategoryMapper.selectByPrimaryKey(id);
            //当前节点的所有正常状态子节点
            TbContentCategoryExample childrenExample = new TbContentCategoryExample();
            childrenExample.createCriteria().andParentIdEqualTo(current.getParentId()).andStatusEqualTo(1);
            List<TbContentCategory> childrenList = tbContentCategoryMapper.selectByExample(childrenExample);

            if(childrenList != null && childrenList.size() == 0){
                TbContentCategory parent = new TbContentCategory();
                tbContentCategory.setId(current.getParentId());
                tbContentCategory.setIsParent(false);
                tbContentCategory.setUpdated(date);
                int indexParent = tbContentCategoryMapper.updateByPrimaryKeySelective(parent);
                if(indexParent != 1){
                    throw  new DaoException("删除内容类目修改父节点is_parent失败");
                }
            }
            return 1;
        }

        throw new DaoException("删除内容类目失败");
    }


    /**
     * 递归修改类目状态（删除）
     * @param id
     * @param date
     * @throws DaoException
     */
    private void deleteChildrenById(long id, Date date)  throws DaoException{

        TbContentCategoryExample example = new TbContentCategoryExample();
        example.createCriteria().andParentIdEqualTo(id).andStatusEqualTo(1);
        //查询子类目
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);

        for(TbContentCategory category : list){
            //删除子类目
            TbContentCategory updateCategory = new TbContentCategory();
            updateCategory.setId(category.getId());
            updateCategory.setStatus(2);
            updateCategory.setUpdated(date);
            int index = tbContentCategoryMapper.updateByPrimaryKeySelective(updateCategory);

            if(index == 1){
                if(category.getIsParent()){
                    //如果子类目为父节点，继续递归
                    deleteChildrenById(category.getId(),date);
                }
            }else{
                throw new DaoException("删除内容类目更新状态失败");
            }
        }
    }
}
