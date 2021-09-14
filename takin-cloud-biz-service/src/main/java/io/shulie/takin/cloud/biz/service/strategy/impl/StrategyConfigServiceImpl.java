package io.shulie.takin.cloud.biz.service.strategy.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import io.shulie.takin.ext.api.EngineCallExtApi;
import io.shulie.takin.cloud.common.utils.EnginePluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import io.shulie.takin.ext.content.enginecall.StrategyConfigExt;
import io.shulie.takin.ext.content.enginecall.StrategyOutputExt;
import io.shulie.takin.cloud.data.dao.strategy.StrategyConfigDAO;
import io.shulie.takin.cloud.data.model.mysql.StrategyConfigEntity;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigAddVO;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigUpdateVO;
import io.shulie.takin.cloud.common.enums.deployment.DeploymentMethodEnum;
import com.pamirs.takin.entity.domain.dto.strategy.StrategyConfigDetailDTO;

/**
 * @author qianshui
 * @date 2020/5/9 下午3:17
 */
@Slf4j
@Service
public class StrategyConfigServiceImpl implements StrategyConfigService {
    @Resource
    StrategyConfigDAO strategyConfigDao;
    @Autowired
    private EnginePluginUtils pluginUtils;

    @Override
    public Boolean add(StrategyConfigAddVO addVO) {
        StrategyConfigEntity config = new StrategyConfigEntity();
        config.setStrategyName(addVO.getStrategyName());
        config.setStrategyConfig(addVO.getStrategyConfig());
        strategyConfigDao.insert(config);
        return true;
    }

    @Override
    public Boolean update(StrategyConfigUpdateVO updateVO) {
        StrategyConfigEntity config = new StrategyConfigEntity();
        config.setId(updateVO.getId());
        config.setStrategyName(updateVO.getStrategyName());
        config.setStrategyConfig(updateVO.getStrategyConfig());
        strategyConfigDao.updateByPrimaryKeySelective(config);
        return true;
    }

    @Override
    public Boolean delete(Long id) {
        return strategyConfigDao.deleteByPrimaryKey(id);
    }

    @Override
    public StrategyConfigDetailDTO getDetail(Long id) {
        StrategyConfigEntity strategyConfig = strategyConfigDao.selectByPrimaryKey(id);
        if (strategyConfig == null) {return null;}
        StrategyConfigDetailDTO dto = new StrategyConfigDetailDTO();
        dto.setStrategyName(strategyConfig.getStrategyName());
        dto.setStrategyConfig(strategyConfig.getStrategyConfig());
        return dto;
    }

    @Override
    public StrategyOutputExt getStrategy(Integer expectThroughput, Integer tpsNum) {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        StrategyConfigExt strategyConfigExt = new StrategyConfigExt();
        strategyConfigExt.setThreadNum(expectThroughput);
        strategyConfigExt.setTpsNum(tpsNum);
        return engineCallExtApi.getPressureNodeNumRange(strategyConfigExt);

    }

    @Override
    public StrategyConfigExt getDefaultStrategyConfig() {
        EngineCallExtApi engineCallExtApi = pluginUtils.getEngineCallExtApi();
        return engineCallExtApi.getDefaultStrategyConfig();
    }

    @Override
    public PageInfo<StrategyConfigExt> queryPageList(int pageNumber, int pageSize) {
        return strategyConfigDao.queryPageList(pageNumber, pageSize, t -> {
            StrategyConfigExt dto = new StrategyConfigExt();
            dto.setId(t.getId());
            dto.setStrategyName(t.getStrategyName());
            parseConfig(dto, t.getStrategyConfig());
            dto.setUpdateTime(DateUtil.formatDateTime(t.getUpdateTime()));
            return dto;
        });
    }

    private void parseConfig(StrategyConfigExt dto, String config) {
        try {
            JSONObject object = JSON.parseObject(config);
            dto.setThreadNum(object.getInteger("threadNum"));
            //默认2cpu
            BigDecimal cpuNum = object.getBigDecimal("cpuNum");
            dto.setCpuNum(cpuNum == null ? new BigDecimal(2) : cpuNum);
            //默认3G内存
            BigDecimal memorySize = object.getBigDecimal("memorySize");
            dto.setMemorySize(memorySize == null ? new BigDecimal(3072) : memorySize);
            //限制cpu 不填则默认为cpuNum
            BigDecimal limitCpuNum = object.getBigDecimal("limitCpuNum");
            dto.setLimitCpuNum(limitCpuNum == null ? cpuNum : limitCpuNum);
            //限制内存 不填默认为memorySize
            BigDecimal limitMemorySize = object.getBigDecimal("limitMemorySize");
            dto.setLimitMemorySize(limitMemorySize == null ? memorySize : limitMemorySize);
            dto.setTpsNum(object.getInteger("tpsNum"));
            dto.setDeploymentMethod(DeploymentMethodEnum.getByType(object.getInteger("deploymentMethod")));
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：解析配置失败 --> Parse Config Failure = {}，异常信息: {}",
                TakinCloudExceptionEnum.SCHEDULE_START_ERROR, config, e);
        }
    }
}
