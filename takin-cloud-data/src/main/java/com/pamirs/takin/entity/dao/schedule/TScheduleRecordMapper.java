package com.pamirs.takin.entity.dao.schedule;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.schedule.ScheduleRecord;
import com.pamirs.takin.entity.domain.vo.schedule.ScheduleRecordQueryVO;

public interface TScheduleRecordMapper {

    int insertSelective(ScheduleRecord record);

    List<ScheduleRecord> getPageList(ScheduleRecordQueryVO queryVO);

    ScheduleRecord getScheduleByTaskId(Long taskId);

}
