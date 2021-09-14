package io.shulie.takin.cloud.data.dao.report.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.shulie.takin.cloud.data.dao.report.ReportDAO;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.common.constants.ReportConstans;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.cloud.data.param.report.ReportDataQueryParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.cloud.data.param.report.ReportUpdateConclusionParam;

import static com.sun.tools.doclint.Entity.and;

/**
 * @author 无涯
 * @date 2020/12/17 3:31 下午
 */
@Service
public class ReportDAOImpl implements ReportDAO {

    @Resource
    private ReportMapper reportMapper;

    /**
     * 插入数据
     *
     * @param record 数据内容
     * @return 插入结果
     */
    @Override
    public boolean insertSelective(ReportEntity record) {
        return reportMapper.insert(record) == 1;
    }

    /**
     * 根据主键查询信息
     *
     * @param id 任务主键
     * @return 报告信息
     */
    @Override
    public ReportEntity selectByPrimaryKey(Long id) {
        return reportMapper.selectById(id);
    }

    /**
     * 获取已经生成报告的场景ID
     *
     * @param sceneIds 场景主键集合
     * @return 运行中的场景主键集合
     */
    @Override
    public Collection<Long> listReportSceneIds(List<Long> sceneIds) {
        QueryWrapper<ReportEntity> wrapper = new QueryWrapper<ReportEntity>()
            .select("DISTINCT scene_id")
            .eq("`status`", 2).eq("is_deleted", false)
            .in("scene_id", sceneIds);
        return reportMapper.selectList(wrapper).stream().map(ReportEntity::getSceneId).collect(Collectors.toSet());
    }

    @Override
    public List<ReportResult> getList(ReportDataQueryParam param) {
        LambdaQueryWrapper<ReportEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(param.getEndTime())) {
            wrapper.le(ReportEntity::getGmtCreate, param.getEndTime());
        }
        List<ReportEntity> entities = reportMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            return entities.stream().map(entity -> {
                ReportResult reportResult = new ReportResult();
                BeanUtils.copyProperties(entity, reportResult);
                return reportResult;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }

    @Override
    public ReportResult selectById(Long id) {
        ReportEntity entity = reportMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        ReportResult reportResult = new ReportResult();
        BeanUtils.copyProperties(entity, reportResult);
        return reportResult;
    }

    /**
     * 获取最新一条报告id
     *
     * @param sceneId 场景主键
     * @return -
     */
    @Override
    public ReportResult getRecentlyReport(Long sceneId) {
        LambdaQueryWrapper<ReportEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportEntity::getSceneId, sceneId);
        wrapper.orderByDesc(ReportEntity::getId);
        wrapper.last("limit 1");
        List<ReportEntity> entities = reportMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            ReportResult reportResult = new ReportResult();
            BeanUtils.copyProperties(entities.get(0), reportResult);
            return reportResult;
        }
        return null;
    }

    @Override
    public void updateReportConclusion(ReportUpdateConclusionParam param) {
        ReportEntity entity = new ReportEntity();
        BeanUtils.copyProperties(param, entity);
        reportMapper.updateById(entity);
    }

    @Override
    public void updateReport(ReportUpdateParam param) {
        ReportEntity entity = new ReportEntity();
        BeanUtils.copyProperties(param, entity);
        reportMapper.updateById(entity);
    }

    @Override
    public void finishReport(Long reportId) {
        ReportEntity entity = new ReportEntity();
        entity.setId(reportId);
        entity.setStatus(ReportConstans.FINISH_STATUS);
        entity.setGmtUpdate(new Date());
        reportMapper.updateById(entity);
    }

    @Override
    public void updateReportLock(Long resultId, Integer lock) {
        ReportEntity entity = new ReportEntity();
        entity.setId(resultId);
        entity.setLock(lock);
        entity.setGmtUpdate(new Date());
        reportMapper.updateById(entity);
    }

    @Override
    public ReportResult getTempReportBySceneId(Long sceneId) {
        LambdaQueryWrapper<ReportEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportEntity::getSceneId, sceneId);
        wrapper.eq(ReportEntity::getIsDeleted, 0);
        wrapper.orderByDesc(ReportEntity::getId);
        wrapper.last("limit 1");
        List<ReportEntity> entities = reportMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            ReportResult reportResult = new ReportResult();
            BeanUtils.copyProperties(entities.get(0), reportResult);
            return reportResult;
        }
        return null;
    }

    @Override
    public ReportResult getReportBySceneId(Long sceneId) {
        LambdaQueryWrapper<ReportEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReportEntity::getSceneId, sceneId);
        // 根据状态
        wrapper.eq(ReportEntity::getStatus, 0);
        wrapper.eq(ReportEntity::getIsDeleted, 0);
        wrapper.orderByDesc(ReportEntity::getId);
        wrapper.last("limit 1");
        List<ReportEntity> entities = reportMapper.selectList(wrapper);
        if (entities != null && entities.size() > 0) {
            ReportResult reportResult = new ReportResult();
            BeanUtils.copyProperties(entities.get(0), reportResult);
            return reportResult;
        }
        return null;
    }

    /**
     * 重置状态
     *
     * @param sceneId 场景主键
     * @return 重置结果
     */
    @Override
    public boolean resumeStatus(Long sceneId) {
        return reportMapper.update(
            new ReportEntity() {{setStatus(2);}},
            new LambdaQueryWrapper<ReportEntity>()
                .eq(ReportEntity::getSceneId, sceneId)) == 1;
    }

    /**
     * 更新启动时间<br>引擎启动，才更新开始时间
     *
     * @param id        报告主键
     * @param startTime 启动时间
     * @return 更新结果
     */
    @Override
    public boolean updateStartTime(Long id, Date startTime) {
        return reportMapper.updateById(new ReportEntity() {{
            setId(id);
            setStartTime(startTime);
        }}) == 1;
    }

    /**
     * 更新数据
     *
     * @param record 新的数据
     * @return 更新结果
     */
    @Override
    public boolean updateByPrimaryKeySelective(ReportEntity record) {
        return reportMapper.updateById(record) == 1;
    }

    /**
     * 更新报告状态
     *
     * @param id          报告主键
     * @param afterStatus 要设定的状态
     * @param preStatus   要前置判断的状态
     * @return 更新结果
     */
    @Override
    public boolean updateReportStatus(Long id, Integer afterStatus, Integer preStatus) {
        LambdaQueryWrapper<ReportEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ReportEntity::getId, id);
        wrapper.eq(preStatus != null, ReportEntity::getStatus, preStatus);
        return reportMapper.update(new ReportEntity() {{setStatus(afterStatus);}}, wrapper) == 1;
    }

    /**
     * 更新报告锁<br>操作lock字段
     *
     * @param id          报告主键
     * @param afterStatus 要设定的状态
     * @param preStatus   要前置判断的状态
     * @return 更新结果
     */
    @Override
    public boolean updateReportLock(Long id, Integer afterStatus, Integer preStatus) {
        LambdaQueryWrapper<ReportEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ReportEntity::getId, id);
        wrapper.eq(ReportEntity::getLock, preStatus);
        return reportMapper.update(new ReportEntity() {{setLock(afterStatus);}}, wrapper) == 1;
    }
}
