package io.shulie.takin.cloud.data.dao.report;

import java.util.Map;
import java.util.List;

import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;

/**
 * 压测报告业务活动详情
 *
 * @author 张天赐
 */
public interface ReportBusinessActivityDetailDAO {

    /**
     * 插入数据
     *
     * @param record 数据内容
     * @return 插入结果
     */
    boolean insertSelective(ReportBusinessActivityDetailEntity record);

    /**
     * 更新数据
     *
     * @param record 新的数据内容<br>需要包含主键信息
     * @return 更新结果
     */
    boolean updateByPrimaryKeySelective(ReportBusinessActivityDetailEntity record);

    /**
     * 根据报告主键查询报告的业务活动详情
     *
     * @param reportId 报告主键
     * @return 业务活动详情
     */
    List<ReportBusinessActivityDetailEntity> queryReportBusinessActivityDetailByReportId(Long reportId);

    /**
     * 获取报告下的业务活动详情的总数
     *
     * @param reportId 报告主键
     * @return 业务活动详情数量
     */
    Map<String, Object> selectCountByReportId(Long reportId);
}
