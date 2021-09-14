package com.pamirs.takin.entity.dao.scene.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneSlaRef;

public interface TSceneSlaRefMapper {

    int deleteBySceneId(Long sceneId);

    void batchInsert(@Param("items") List<SceneSlaRef> records);

    List<SceneSlaRef> selectBySceneId(Long sceneId);

}
