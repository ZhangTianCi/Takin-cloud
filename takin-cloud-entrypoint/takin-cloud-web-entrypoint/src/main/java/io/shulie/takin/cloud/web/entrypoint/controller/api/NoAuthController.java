package io.shulie.takin.cloud.web.entrypoint.controller.api;

import java.util.Map;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import io.shulie.takin.cloud.data.dao.report.ReportDAO;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.shulie.takin.common.beans.response.ResponseResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;

/**
 * @author qianshui
 * @date 2020/8/30 下午2:08
 */
@Slf4j
@RestController
@RequestMapping("/api/noauth")
public class NoAuthController {
    @Resource
    private ReportDAO reportDao;
    @Resource
    private SceneManageDAO sceneManageDao;

    /**
     * 恢复压测中的场景状态
     * update t_scene_manage set `status`=0 where id=？;
     * update t_report set `status`=2 where scene_id=？;
     *
     * @param paramMap 参数
     * @return -
     */
    @PutMapping("/resume/scenetask")
    public ResponseResult<String> resumeSceneTask(@RequestBody Map<String, Object> paramMap) {
        if (paramMap == null || paramMap.get("sceneId") == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.TASK_RUNNING_PARAM_VERIFY_ERROR, "sceneId cannot be null");
        }
        Long sceneId = Long.parseLong(String.valueOf(paramMap.get("sceneId")));
        boolean reportResumeResult = reportDao.resumeStatus(sceneId);
        log.debug("报告重置结果:{}", reportResumeResult);
        boolean resumeResult = sceneManageDao.resumeStatus(sceneId);
        log.debug("场景重置结果:{}", resumeResult);
        return ResponseResult.success("resume success");
    }
}
