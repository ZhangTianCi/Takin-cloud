package io.shulie.takin.cloud.data.dao.scene.manage.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.cloud.data.mapper.mysql.SceneBusinessActivityRefMapper;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneBusinessActivityRefDAO;

/**
 * {@link SceneBusinessActivityRefDAO}接口的实现类
 *
 * @author 张天赐
 */
@Component
public class SceneBusinessActivityRefDAOImpl implements SceneBusinessActivityRefDAO {
    SceneBusinessActivityRefMapper sceneBusinessActivityRefMapper;

    /**
     * 根据场景主键删除数据
     *
     * @param sceneId 场景主键
     * @return 删除行数
     */
    @Override
    public int deleteBySceneId(Long sceneId) {
        UpdateWrapper<SceneBusinessActivityRefEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("scene_id", sceneId);
        return sceneBusinessActivityRefMapper.delete(wrapper);
    }

    /**
     * 批量插入
     *
     * @param records 数据源
     */
    @Override
    public void batchInsert(List<SceneBusinessActivityRefEntity> records) {
        sceneBusinessActivityRefMapper.batchInsert(records);
    }

    /**
     * 根据场景主键查询
     *
     * @param sceneId 场景主键
     * @return 相关的数据项
     */
    @Override
    public List<SceneBusinessActivityRefEntity> selectBySceneId(Long sceneId) {
        QueryWrapper<SceneBusinessActivityRefEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("scene_id", sceneId);
        return sceneBusinessActivityRefMapper.selectList(wrapper);

    }
}
