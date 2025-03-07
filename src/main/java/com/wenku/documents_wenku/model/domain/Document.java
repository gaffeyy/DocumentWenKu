package com.wenku.documents_wenku.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 
 * @TableName document
 */
@TableName(value ="document")
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Document implements Serializable {
    /**
     * 资料id(主键)
     */
    @TableId(value = "documentId", type = IdType.AUTO)
    private Long documentId;

    /**
     * 资料名称
     */
    @TableField(value = "documentName")
    private String documentName;

    /**
     * 资料类型
     */
    @TableField(value = "category")
    private String category;

    /**
     * 上传用户ID
     */
    @TableField(value = "uploadUserId")
    private Long uploadUserId;

    /**
     * 上传时间
     */
    @TableField(value = "uploadTime")
    private Date uploadTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * 文档URL
     */
    @JsonProperty("documentUrl")
    @TableField(value = "documentUrl")
    private String documentUrl;

    /**
     * 文档标签（JSON列表）
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 点赞数
     */
    @TableField(value = "likes")
    private Long likes;

    /**
     * 浏览记录
     */
    private Long browser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}