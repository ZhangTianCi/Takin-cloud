package io.shulie.takin.cloud.data.dao.report;

import java.util.Date;
import java.util.List;
import java.util.Collection;

import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.param.report.ReportDataQueryParam;
import io.shulie.takin.cloud.data.param.report.ReportUpdateConclusionParam;

/**
 * @author 无涯
 * @date 2020/12/17 3:30 下午
 */
public interface ReportDAO {
    /**
     * 插入数据
     *
     * @param record 数据内容
     * @return 插入结果
     */
    boolean insertSelective(ReportEntity record);

    /**
     * 根据主键查询信息
     *
     * @param id 任务主键
     * @return 报告信息
     */
    ReportEntity selectByPrimaryKey(Long id);

    /**
     * 获取已经生成报告的场景ID
     *
     * @param sceneIds 场景主键集合
     * @return -
     */
    Collection<Long> listReportSceneIds(List<Long> sceneIds);

    /**
     * 获取列表
     *
     * @param param -
     * @return -
     */
    List<ReportResult> getList(ReportDataQueryParam param);

    /**
     * 获取报告
     *
     * @param id 报告主键
     * @return -
     */
    ReportResult selectById(Long id);

    /**
     * 获取当前场景最新一条报告
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getRecentlyReport(Long sceneId);

    /**
     * 更新通过是否通过
     *
     * @param param 入参
     */
    void updateReportConclusion(ReportUpdateConclusionParam param);

    /**
     * 更新报告
     *
     * @param param 参数
     */
    void updateReport(ReportUpdateParam param);

    /**
     * 完成报告
     *
     * @param reportId 报告主键
     */
    void finishReport(Long reportId);

    /**
     * 锁报告
     *
     * @param resultId -
     * @param lock     -
     */
    void updateReportLock(Long resultId, Integer lock);

    /**
     * 根据场景ID获取（临时）压测中的报告ID
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getTempReportBySceneId(Long sceneId);

    /**
     * 根据场景ID获取压测中的报告ID
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getReportBySceneId(Long sceneId);

    /**
     * 重置状态
     *
     * @param sceneId 场景主键
     * @return 重置结果
     */
    boolean resumeStatus(Long sceneId);

    /**
     * 更新启动时间<br>引擎启动，才更新开始时间
     *
     * @param id        报告主键
     * @param startTime 启动时间
     * @return 更新结果
     */
    boolean updateStartTime(Long id, Date startTime);

    /**
     * 更新数据
     *
     * @param record 新的数据
     * @return 更新结果
     */
    boolean updateByPrimaryKeySelective(ReportEntity record);

    /**
     * 更新报告状态
     *
     * @param id          报告主键
     * @param afterStatus 要设定的状态
     * @param preStatus   要前置判断的状态
     * @return 更新结果
     */
    boolean updateReportStatus(Long id, Integer afterStatus, Integer preStatus);

    /**
     * 更新报告锁<br>操作lock字段
     *
     * @param id          报告主键
     * @param afterStatus 要设定的状态
     * @param preStatus   要前置判断的状态
     * @return 更新结果
     */
    boolean updateReportLock(Long id, Integer afterStatus, Integer preStatus);
}
