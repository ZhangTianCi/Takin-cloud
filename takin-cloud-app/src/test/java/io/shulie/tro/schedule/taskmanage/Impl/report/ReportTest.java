package io.shulie.tro.schedule.taskmanage.Impl.report;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import io.shulie.takin.app.Application;
import io.shulie.takin.cloud.data.dao.report.ReportDAO;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import org.springframework.boot.test.context.SpringBootTest;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.common.bean.sla.WarnQueryParam;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import io.shulie.takin.cloud.biz.service.report.ReportService;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import com.pamirs.takin.entity.dao.scene.manage.TWarnDetailMapper;
import com.pamirs.takin.entity.domain.entity.scene.manage.WarnDetail;

/**
 * @author mubai
 * @date 2020-11-09 17:27
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class ReportTest {

    @Resource
    private TWarnDetailMapper tWarnDetailMapper;

    @Resource
    private ReportMapper reportMapper;

    @Autowired
    private ReportService reportService;

    @Resource
    private ReportDAO reportDao;

    @Test
    public void testQuery() {

        WarnQueryParam param = new WarnQueryParam();
        param.setReportId(612L);
        List<WarnDetail> warnDetails = tWarnDetailMapper.listWarn(param);
        System.out.println(warnDetails);
    }

    @Test
    public void testReport() {
        ReportEntity reportEntity = reportMapper.selectById(610L);
        System.out.println(reportEntity);
    }

    @Test
    public void testTreport() {
        ReportEntity report = reportDao.selectByPrimaryKey(610L);
        System.out.println(report);
    }

    @Test
    public void testService() {
        ReportDetailOutput reportByReportId = reportService.getReportByReportId(610L);
        System.out.println(reportByReportId);
    }
}
