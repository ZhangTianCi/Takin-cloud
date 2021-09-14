package io.shulie.takin.cloud.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneBusinessActivityRef;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import org.apache.ibatis.annotations.Param;

public interface SceneBusinessActivityRefMapper extends BaseMapper<SceneBusinessActivityRefEntity> {
    void batchInsert(@Param("items") List<SceneBusinessActivityRefEntity> items);
}