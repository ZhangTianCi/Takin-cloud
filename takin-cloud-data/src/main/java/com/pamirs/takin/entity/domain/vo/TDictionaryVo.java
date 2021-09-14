package com.pamirs.takin.entity.domain.vo;

import java.util.Date;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.shulie.takin.cloud.common.serialize.DateToStringFormatSerialize;

/**
 * 说明：数据字典模型类
 *
 * @author shulie
 * @version 1.0
 * @date 2018/11/1 0001 17:32
 */
@Data
public class TDictionaryVo {
    /**
     * 字典ID
     */
    private String typeId;
    /**
     * 字典名称
     */
    private String typeName;
    /**
     * 字典别名
     */
    private String typeAlias;
    /**
     * 字典可维护标志
     */
    private String typeActive;
    /**
     * 是否是叶子
     */
    private String isLeaf;

    /**
     * 数据字典值ID
     */
    private String valueId;
    /**
     * 值顺序
     */
    private String valueOrder;
    /**
     * 值名称
     */
    private String valueName;
    /**
     * 值编码
     */
    private String valueCode;
    /**
     * 值是否可用
     */
    private String valueActive;
    /**
     * 值语言，默认中文
     */
    private String language = "ZH_CN";
    /**
     * 值创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date createTime;
    /**
     * 值修改时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date modifyTime;
}
