package com.pamirs.takin.entity.dao.scene.manage;

import java.util.List;

import com.pamirs.takin.entity.domain.bo.scenemanage.WarnBO;
import com.pamirs.takin.entity.domain.entity.scene.manage.WarnDetail;
import io.shulie.takin.cloud.common.bean.sla.WarnQueryParam;

public interface TWarnDetailMapper {

    int insertSelective(WarnDetail record);

    /**
     * 警告汇总
     *
     * @param reportId 报告主键
     * @return -
     */
    List<WarnBO> summaryWarnByReportId(Long reportId);

    /**
     * 警告列表
     *
     * @param param -
     * @return -
     */
    List<WarnDetail> listWarn(WarnQueryParam param);

    /**
     * 统计报告总警告次数
     *
     * @param reportId 报告主键
     * @return -
     */
    Long countReportTotalWarn(Long reportId);

}
