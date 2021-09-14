package io.shulie.takin.cloud.biz.service.strategy;

import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import io.shulie.takin.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.ext.content.enginecall.StrategyOutputExt;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigAddVO;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigUpdateVO;
import com.pamirs.takin.entity.domain.dto.strategy.StrategyConfigDetailDTO;

/**
 * @author qianshui
 * @date 2020/5/9 下午3:16
 */
public interface StrategyConfigService {

    Boolean add(StrategyConfigAddVO addVO);

    Boolean update(StrategyConfigUpdateVO updateVO);

    Boolean delete(@Param("id") Long id);

    PageInfo<StrategyConfigExt> queryPageList(int pageNumber, int pageSize);

    StrategyConfigDetailDTO getDetail(Long id);

    /**
     * 根据最大并发获得策略结果
     *
     * @param expectThroughput -
     * @param tpsNum           -
     * @return -
     */
    StrategyOutputExt getStrategy(Integer expectThroughput, Integer tpsNum);

    /**
     * 获取默认策略
     *
     * @return -
     */
    StrategyConfigExt getDefaultStrategyConfig();
}
