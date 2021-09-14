

package io.shulie.takin.cloud.biz.service.sla.impl;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import io.shulie.takin.cloud.biz.utils.SlaUtil;
import io.shulie.takin.cloud.biz.event.SlaPublish;
import io.shulie.takin.cloud.common.bean.sla.SlaBean;
import org.apache.commons.collections4.CollectionUtils;
import io.shulie.takin.cloud.biz.service.sla.SlaService;
import io.shulie.takin.cloud.common.constants.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.shulie.takin.cloud.common.bean.sla.AchieveModel;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import com.pamirs.takin.entity.dao.scene.manage.TWarnDetailMapper;
import io.shulie.takin.cloud.biz.service.scene.SceneManageService;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import com.pamirs.takin.entity.domain.entity.scene.manage.WarnDetail;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;
import io.shulie.takin.ext.content.enginecall.ScheduleStopRequestExt;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.result.scenemanage.SceneSlaRefResult;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOpitons;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageWrapperResult;

/**
 * @author qianshui
 * @date 2020/4/20 下午4:48
 */
@Service
@Slf4j
public class SlaServiceImpl implements SlaService {

    public static final String SLA_SCENE_KEY = "TRO:SLA:SCENE:KEY";
    public static final String SLA_DESTROY_KEY = "TRO:SLA:DESTROY:KEY";
    public static final String SLA_WARN_KEY = "TRO:SLA:WARN:KEY";
    public static final Long EXPIRE_TIME = 24 * 3600L;
    public static final String PREFIX_TASK = "TRO:SLA:TASK:";
    @Resource
    private SlaPublish slaPublish;
    @Resource
    private ReportService reportService;
    @Resource
    private RedisClientUtils redisClientUtils;
    @Resource
    private TWarnDetailMapper tWarnDetailMapper;
    @Resource
    private SceneManageService sceneManageService;

    @Override
    public Boolean buildWarn(SendMetricsEvent metricsEvent) {
        if (StringUtils.isBlank(metricsEvent.getTransaction())
            || "all".equalsIgnoreCase(metricsEvent.getTransaction())) {
            return true;
        }
        Long sceneId = metricsEvent.getSceneId();
        SceneManageWrapperOutput dto;
        try {
            dto = getSceneManageWrapperDTO(sceneId);
            if (dto == null) {
                log.error("异常代码【{}】,异常内容：构建sla异常 --> 未找到压测场景， SendMetricsEvent={}",
                    TakinCloudExceptionEnum.TASK_START_BUILD_SAL, JSON.toJSONString(metricsEvent));
                return false;
            }
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：构建sla异常 -->  查找到压测场景异常， SendMetricsEvent={}，异常信息:{}",
                TakinCloudExceptionEnum.TASK_START_BUILD_SAL, JSON.toJSONString(metricsEvent), e);
            return false;
        }
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivity = dto.getBusinessActivityConfig().stream().filter(
            data -> metricsEvent.getTransaction().equals(data.getBindRef())).findFirst().orElse(null);

        if (businessActivity == null) {
            log.error("异常代码【{}】,异常内容：构建sla异常 --> 未找到业务活动， SendMetricsEvent={}",
                TakinCloudExceptionEnum.TASK_START_BUILD_SAL, JSON.toJSONString(metricsEvent));
            return false;
        }

        Long businessActivityId = businessActivity.getBusinessActivityId();

        doDestroy(dto.getId(), metricsEvent, filterSlaList(businessActivityId, dto.getStopCondition()), businessActivity);

        doWarn(businessActivity, metricsEvent, filterSlaList(businessActivityId, dto.getWarningCondition()));

        return true;
    }

    @Override
    public void removeMap(Long sceneId) {
        String scene = (String)redisClientUtils.hmget(SLA_SCENE_KEY, String.valueOf(sceneId));
        if (scene == null) {
            return;
        }
        SceneManageWrapperResult dto = JSON.parseObject(scene, SceneManageWrapperResult.class);

        dto.getStopCondition().stream().map(SceneSlaRefResult::getId).forEach(
            id -> redisClientUtils.hmdelete(SLA_DESTROY_KEY, String.valueOf(id)));
        dto.getWarningCondition().stream().map(SceneSlaRefResult::getId).forEach(
            id -> redisClientUtils.hmdelete(SLA_WARN_KEY, String.valueOf(id)));
        redisClientUtils.hmdelete(SLA_SCENE_KEY, String.valueOf(sceneId));
        redisClientUtils.delete(PREFIX_TASK + sceneId);
        log.info("清除SLA分析内存配置成功, sceneId={}", sceneId);
    }

    @Override
    public void cacheData(Long sceneId) {
        redisClientUtils.setString(PREFIX_TASK + sceneId, "on", 7, TimeUnit.DAYS);
    }

    private void doDestroy(Long sceneId, SendMetricsEvent metricsEvent, List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList, SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO) {
        if (CollectionUtils.isEmpty(slaList)) {
            return;
        }
        slaList.forEach(dto -> {
            SceneSlaRefInput input = new SceneSlaRefInput();
            BeanUtils.copyProperties(dto, input);
            Map<String, Object> conditionMap = SlaUtil.matchCondition(input, metricsEvent);
            if (!(Boolean)conditionMap.get("result")) {
                redisClientUtils.hmdelete(SLA_DESTROY_KEY, String.valueOf(dto.getId()));
                return;
            }
            String object = (String)redisClientUtils.hmget(SLA_DESTROY_KEY, String.valueOf(dto.getId()));
            AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
            if (!matchContinue(model, metricsEvent.getTimestamp())) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put(String.valueOf(dto.getId()),
                    JSON.toJSONString(new AchieveModel(1, metricsEvent.getTimestamp())));
                redisClientUtils.hmset(SLA_DESTROY_KEY, dataMap, EXPIRE_TIME);
                return;
            }
            model.setTimes(model.getTimes() + 1);
            model.setLastAchieveTime(metricsEvent.getTimestamp());
            if (model.getTimes() >= dto.getRule().getTimes()) {
                try {
                    ScheduleStopRequestExt scheduleStopRequest = new ScheduleStopRequestExt();
                    scheduleStopRequest.setTaskId(metricsEvent.getReportId());
                    scheduleStopRequest.setSceneId(sceneId);
                    // 增加顾客id
                    scheduleStopRequest.setCustomerId(metricsEvent.getCustomerId());
                    Map<String, Object> extendMap = Maps.newHashMap();
                    extendMap.put(Constants.SLA_DESTORY_EXTEND, "SLA发送压测任务终止事件");
                    scheduleStopRequest.setExtend(extendMap);
                    //报告未结束，才通知
                    if (redisClientUtils.hasKey(PREFIX_TASK + metricsEvent.getSceneId())) {
                        // 熔断数据也记录到告警明细中
                        WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivityDTO, metricsEvent, dto);
                        tWarnDetailMapper.insertSelective(warnDetail);
                        // 记录sla熔断数据
                        UpdateReportSlaDataInput slaDataInput = new UpdateReportSlaDataInput();
                        SlaBean slaBean = new SlaBean();
                        slaBean.setRuleName(dto.getRuleName());
                        slaBean.setBusinessActivity(businessActivityDTO.getBusinessActivityName());
                        slaBean.setRule(warnDetail.getWarnContent());
                        slaDataInput.setReportId(scheduleStopRequest.getTaskId());
                        slaDataInput.setSlaBean(slaBean);
                        reportService.updateReportSlaData(slaDataInput);
                        // 触发中止方法
                        slaPublish.stop(scheduleStopRequest);
                        log.warn("【SLA】成功发送压测任务终止事件，并记录sla熔断数据");
                    }
                } catch (Exception e) {
                    log.warn("【SLA】发送压测任务终止事件失败:{}", e.getMessage(), e);
                }
            } else {
                redisClientUtils.hmset(SLA_DESTROY_KEY, String.valueOf(dto.getId()), JSON.toJSONString(model));
            }
        });
    }

    private void doWarn(SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO,
        SendMetricsEvent metricsEvent, List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList) {
        if (CollectionUtils.isEmpty(slaList)) {
            return;
        }
        slaList.forEach(dto -> {
            SceneSlaRefInput input = new SceneSlaRefInput();
            BeanUtils.copyProperties(dto, input);
            Map<String, Object> conditionMap = SlaUtil.matchCondition(input, metricsEvent);
            if (!(Boolean)conditionMap.get("result")) {
                redisClientUtils.hmdelete(SLA_WARN_KEY, String.valueOf(dto.getId()));
                return;
            }

            String object = (String)redisClientUtils.hmget(SLA_WARN_KEY, String.valueOf(dto.getId()));
            AchieveModel model = (object != null ? JSON.parseObject(object, AchieveModel.class) : null);
            if (!matchContinue(model, metricsEvent.getTimestamp())) {
                Map<String, Object> dataMap = Maps.newHashMap();
                dataMap.put(String.valueOf(dto.getId()),
                    JSON.toJSONString(new AchieveModel(1, metricsEvent.getTimestamp())));
                redisClientUtils.hmset(SLA_WARN_KEY, dataMap, EXPIRE_TIME);
                return;
            }
            model.setTimes(model.getTimes() + 1);
            model.setLastAchieveTime(metricsEvent.getTimestamp());
            if (model.getTimes() >= dto.getRule().getTimes()) {
                WarnDetail warnDetail = buildWarnDetail(conditionMap, businessActivityDTO, metricsEvent, dto);
                //报告未结束，才insert
                if (redisClientUtils.hasKey(PREFIX_TASK + metricsEvent.getSceneId())) {
                    tWarnDetailMapper.insertSelective(warnDetail);
                }
            } else {
                redisClientUtils.hmset(SLA_WARN_KEY, String.valueOf(dto.getId()), JSON.toJSONString(model));
            }
        });
    }

    private Boolean matchContinue(AchieveModel model, Long timestamp) {
        if (model == null) {
            return false;
        }
        log.info("【sla】校验是否连续，上次触发时间={}, 当前时间={}，相差={}",
            model.getLastAchieveTime(), timestamp,
            (timestamp - model.getLastAchieveTime()));
        return true;
    }

    private WarnDetail buildWarnDetail(Map<String, Object> conditionMap,
        SceneManageWrapperOutput.SceneBusinessActivityRefOutput businessActivityDTO,
        SendMetricsEvent metricsEvent,
        SceneManageWrapperOutput.SceneSlaRefOutput salDTO) {
        WarnDetail warnDetail = new WarnDetail();
        warnDetail.setPtId(metricsEvent.getReportId());
        warnDetail.setSlaId(salDTO.getId());
        warnDetail.setSlaName(salDTO.getRuleName());
        warnDetail.setBusinessActivityId(businessActivityDTO.getBusinessActivityId());
        warnDetail.setBusinessActivityName(businessActivityDTO.getBusinessActivityName());
        String sb = String.valueOf(conditionMap.get("type"))
            + conditionMap.get("compare")
            + salDTO.getRule().getDuring()
            + conditionMap.get("unit") + ", 连续" + salDTO.getRule().getTimes() + "次";
        warnDetail.setWarnContent(sb);
        warnDetail.setWarnTime(DateUtil.date(metricsEvent.getTimestamp()));
        warnDetail.setRealValue((Double)conditionMap.get("real"));
        return warnDetail;
    }

    private List<SceneManageWrapperOutput.SceneSlaRefOutput> filterSlaList(Long businessActivityId, List<SceneManageWrapperOutput.SceneSlaRefOutput> slaList) {
        if (CollectionUtils.isEmpty(slaList)) {
            return new ArrayList<>(0);
        }
        return slaList.stream().filter(data -> checkContain(data.getBusinessActivity(), businessActivityId))
            .collect(Collectors.toList());
    }

    private Boolean checkContain(String[] businessActivity, Long businessActivityId) {
        if (businessActivity == null || businessActivity.length == 0) {
            return false;
        }
        for (String data : businessActivity) {
            if ("-1".equals(data) || String.valueOf(businessActivityId).equals(data)) {
                return true;
            }
        }
        return false;
    }

    private SceneManageWrapperOutput getSceneManageWrapperDTO(Long sceneId) {
        String object = (String)redisClientUtils.hmget(SLA_SCENE_KEY, String.valueOf(sceneId));
        if (object != null) {
            return JSON.parseObject(object, SceneManageWrapperOutput.class);
        }
        SceneManageQueryOpitons options = new SceneManageQueryOpitons();
        options.setIncludeBusinessActivity(true);
        options.setIncludeSLA(true);
        SceneManageWrapperOutput dto = sceneManageService.getSceneManage(sceneId, options);
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put(String.valueOf(sceneId), JSON.toJSONString(dto));
        redisClientUtils.hmset(SLA_SCENE_KEY, dataMap, EXPIRE_TIME);
        return dto;
    }
}
