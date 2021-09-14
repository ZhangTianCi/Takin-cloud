package com.pamirs.takin.entity.dao.scene.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.pamirs.takin.entity.domain.query.SceneScriptRefQueryParam;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;

@Mapper
public interface TSceneScriptRefMapper {

    int deleteByIds(@Param("ids") List<Long> ids);

    Long insertSelective(SceneScriptRef record);

    void batchInsert(@Param("items") List<SceneScriptRef> records);

    SceneScriptRef selectByPrimaryKey(Long id);

    List<SceneScriptRef> selectBySceneIdAndScriptType(@Param("sceneId") Long sceneId,
        @Param("scriptType") Integer scriptType);

    int updateByPrimaryKeySelective(SceneScriptRef record);

    SceneScriptRef selectByExample(SceneScriptRefQueryParam param);

    void deleteBySceneId(@Param("id") Long id);

}
