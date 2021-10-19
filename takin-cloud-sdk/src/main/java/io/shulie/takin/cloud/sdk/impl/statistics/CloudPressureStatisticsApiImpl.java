package io.shulie.takin.cloud.sdk.impl.statistics;

import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import org.springframework.stereotype.Service;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.model.request.statistics.PressureTotalReq;
import io.shulie.takin.cloud.sdk.model.response.statistics.ReportTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.entrypoint.statistics.CloudPressureStatisticsApi;

/**
 * @author 无涯
 * @date 2020/11/30 9:53 下午
 */
@Service
public class CloudPressureStatisticsApiImpl implements CloudPressureStatisticsApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public PressurePieTotalResp getPressurePieTotal(PressureTotalReq req) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE),
            req, new TypeReference<ResponseResult<PressurePieTotalResp>>() {}).getData();
    }

    @Override
    public ReportTotalResp getReportTotal(PressureTotalReq req) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE),
            req, new TypeReference<ResponseResult<ReportTotalResp>>() {}).getData();
    }

    @Override
    public List<PressureListTotalResp> getPressureListTotal(PressureTotalReq req) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_REPORT, EntrypointUrl.METHOD_SCENE_MANAGE_UPDATE),
            req, new TypeReference<ResponseResult<List<PressureListTotalResp>>>() {}).getData();
    }

}
