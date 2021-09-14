package com.pamirs.takin.entity.dao.report;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.pamirs.takin.entity.domain.entity.report.Report;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.common.annotation.DataApartInterceptAnnotation;

public interface TReportMapper {
    int insertSelective(Report record);

    @DataApartInterceptAnnotation
    Report selectOneRunningReport();

    @DataApartInterceptAnnotation
    List<Report> selectListRunningReport();

    /**
     * 报表列表
     *
     * @param param 查询参数
     * @return -
     */
    @DataApartInterceptAnnotation
    List<Report> listReport(@Param("param") ReportQueryParam param);
}
