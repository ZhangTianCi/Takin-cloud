package com.pamirs.takin.entity.dao.dict;

import java.util.Map;
import java.util.List;

import com.pamirs.takin.entity.domain.vo.TDictionaryVo;

public interface TDictionaryDataMapper {
    /**
     * 查询数据字典列表
     *
     * @param paramMap 查询条件
     * @return -
     */
    List<TDictionaryVo> queryDictionaryList(Map<String, Object> paramMap);

}
