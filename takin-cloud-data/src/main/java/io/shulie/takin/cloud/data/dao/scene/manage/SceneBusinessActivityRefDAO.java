package io.shulie.takin.cloud.data.dao.scene.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;

/**
 * 场景业务活动关联
 *
 * @author 张天赐
 */
public interface SceneBusinessActivityRefDAO {
    /**
     * 根据场景主键删除数据
     *
     * @param sceneId 场景主键
     * @return 删除行数
     */
    int deleteBySceneId(Long sceneId);

    /**
     * 批量插入
     *
     * @param records 数据源
     */
    void batchInsert(@Param("items") List<SceneBusinessActivityRefEntity> records);

    /**
     * 根据场景主键查询
     *
     * @param sceneId 场景主键
     * @return 相关的数据项
     */
    List<SceneBusinessActivityRefEntity> selectBySceneId(Long sceneId);
}
