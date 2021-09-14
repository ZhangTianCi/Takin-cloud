package com.pamirs.takin.entity.dao.scene.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneManage;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneManageQueryVO;
import io.shulie.takin.cloud.common.annotation.DataApartInterceptAnnotation;

@Mapper
@Deprecated
public interface TSceneManageMapper {

    @DataApartInterceptAnnotation
    List<SceneManage> getPageList(SceneManageQueryVO queryVO);

    /**
     * 查询所有场景信息
     *
     * @return -
     */
    List<SceneManage> selectAllSceneManageList();

    List<SceneManage> getByIds(@Param("ids") List<Long> ids);

}
