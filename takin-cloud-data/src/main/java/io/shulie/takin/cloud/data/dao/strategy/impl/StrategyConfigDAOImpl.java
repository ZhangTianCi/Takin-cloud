package io.shulie.takin.cloud.data.dao.strategy.impl;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Component;
import io.shulie.takin.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.cloud.data.dao.strategy.StrategyConfigDAO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.cloud.data.model.mysql.StrategyConfigEntity;
import io.shulie.takin.cloud.data.mapper.mysql.StrategyConfigMapper;

/**
 * {@link StrategyConfigDAO}接口的实现类
 *
 * @author 张天赐
 */
@Component
public class StrategyConfigDAOImpl implements StrategyConfigDAO {
    @Resource
    StrategyConfigMapper strategyConfigMapper;

    /**
     * 根据主键删除
     *
     * @param id 数据主键
     * @return 删除结果<br>如果数据本身不存在,也认为删除成功
     */
    @Override
    public boolean deleteByPrimaryKey(Long id) {
        return strategyConfigMapper.deleteById(id) <= 1;
    }

    /**
     * 插入新的数据行
     *
     * @param record 数据内容
     * @return 插入结果
     */
    @Override
    public boolean insert(StrategyConfigEntity record) {
        return strategyConfigMapper.insert(record) == 1;
    }

    /**
     * 根据主键查询数据
     *
     * @param id 数据主键
     * @return 数据内容
     */
    @Override
    public StrategyConfigEntity selectByPrimaryKey(Long id) {
        return strategyConfigMapper.selectById(id);
    }

    /**
     * 更新数据
     *
     * @param record 新的数据内容<br>需要包含主键信息
     * @return 更新结果
     */
    @Override
    public boolean updateByPrimaryKeySelective(StrategyConfigEntity record) {
        return strategyConfigMapper.updateById(record) == 1;
    }

    /**
     * 分页
     *
     * @param pageNumber 分页页码
     * @param pageSize   分页容量
     * @return 分页数据
     */
    @Override
    public PageInfo<StrategyConfigExt> queryPageList(int pageNumber, int pageSize, Function<? super StrategyConfigEntity, ? extends StrategyConfigExt> mapper) {
        Page<?> pageInfo = PageHelper.startPage(pageNumber, pageSize);
        QueryWrapper<StrategyConfigEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", false).orderByDesc("update_time");
        List<StrategyConfigEntity> dbResult = strategyConfigMapper.selectList(wrapper);
        List<StrategyConfigExt> mapResult = dbResult.stream().map(mapper).map(t -> (StrategyConfigExt)t).collect(Collectors.toList());
        return new PageInfo<StrategyConfigExt>(mapResult) {{setTotal(pageInfo.getTotal());}};
    }
}
