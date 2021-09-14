package io.shulie.takin.cloud.data.dao.report.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.shulie.takin.cloud.data.dao.report.ReportBusinessActivityDetailDAO;
import com.pamirs.takin.entity.domain.entity.report.ReportBusinessActivityDetail;
import io.shulie.takin.cloud.data.mapper.mysql.ReportBusinessActivityDetailMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import org.springframework.stereotype.Component;

@Component
public class ReportBusinessActivityDetailDAOImpl implements ReportBusinessActivityDetailDAO {
    ReportBusinessActivityDetailMapper reportBusinessActivityDetailMapper;

    /**
     * 插入数据
     *
     * @param record 数据内容
     * @return 插入结果
     */
    @Override
    public boolean insertSelective(ReportBusinessActivityDetailEntity record) {
        return reportBusinessActivityDetailMapper.insert(record) == 1;
    }

    /**
     * 更新数据
     *
     * @param record 新的数据内容<br>需要包含主键信息
     * @return 更新结果
     */
    @Override
    public boolean updateByPrimaryKeySelective(ReportBusinessActivityDetailEntity record) {
        return reportBusinessActivityDetailMapper.updateById(record) == 1;
    }

    /**
     * 根据报告主键查询报告的业务活动详情
     *
     * @param reportId 报告主键
     * @return 业务活动详情
     */
    @Override
    public List<ReportBusinessActivityDetailEntity> queryReportBusinessActivityDetailByReportId(Long reportId) {
        QueryWrapper<ReportBusinessActivityDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("report_id", reportId);
        return  reportBusinessActivityDetailMapper.selectList(wrapper);
    }

    /**
     * 获取报告下的业务活动详情的总数
     *
     * @param reportId 报告主键
     * @return 业务活动详情数量
     */
    @Override
    public Map<String, Object> selectCountByReportId(Long reportId) {
        QueryWrapper<ReportBusinessActivityDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(id)AS`count`", "SUM(pass_flag)AS`passSum`").eq("report_id", reportId);
        List<Map<String, Object>> mapList = reportBusinessActivityDetailMapper.selectMaps(wrapper);
        return mapList.stream().collect(Collectors.toMap(k -> k.get("count").toString(), v -> v));
    }
}
