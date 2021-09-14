package io.shulie.takin.cloud.data.dao.strategy;

import java.util.function.Function;

import com.github.pagehelper.PageInfo;
import io.shulie.takin.cloud.data.model.mysql.StrategyConfigEntity;
import io.shulie.takin.ext.content.enginecall.StrategyConfigExt;

/**
 * 策略配置
 *
 * @author 张天赐
 */
public interface StrategyConfigDAO {
    /**
     * 根据主键删除
     *
     * @param id 数据主键
     * @return 删除结果
     */
    boolean deleteByPrimaryKey(Long id);

    /**
     * 插入新的数据行
     *
     * @param record 数据内容
     * @return 插入结果
     */
    boolean insert(StrategyConfigEntity record);

    /**
     * 根据主键查询数据
     *
     * @param id 数据主键
     * @return 数据内容
     */
    StrategyConfigEntity selectByPrimaryKey(Long id);

    /**
     * 更新数据
     *
     * @param record 新的数据内容<br>需要包含主键信息
     * @return 更新结果
     */
    boolean updateByPrimaryKeySelective(StrategyConfigEntity record);

    /**
     * 分页
     *
     * @param pageNumber 分页页码
     * @param pageSize   分页容量
     * @param mapper     stream.map函数
     * @return 分页数据
     */
    PageInfo<StrategyConfigExt> queryPageList(int pageNumber, int pageSize, Function<? super StrategyConfigEntity, ? extends StrategyConfigExt> mapper);
}
