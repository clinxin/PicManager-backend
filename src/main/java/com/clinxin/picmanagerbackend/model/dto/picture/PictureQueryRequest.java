package com.clinxin.picmanagerbackend.model.dto.picture;

import com.clinxin.picmanagerbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图片查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 文件体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 搜索词（同时搜名称、简介等）
     */
    private String searchText;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     * 补充：新增字段
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     * 补充：新增字段
     */
    private String reviewMessage;

    /**
     * 审核人 id
     * 补充：新增字段
     */
    private Long reviewerId;

    /**
     * 审核时间
     * 补充：新增字段
     */
    private Date reviewerTime;

    /**
     * 空间 id
     * 补充：新增字段，空间相关
     */
    private Long spaceId;

    /**
     * 是否只查询 spaceId 为 null 的数据
     * 补充：新增字段，空间相关
     */
    private boolean nullSpaceId;

    /**
     * 开始编辑时间
     * 补充：图片搜索优化
     */
    private Date startEditTime;

    /**
     * 结束编辑时间
     * 补充：图片搜索优化
     */
    private Date endEditTime;

    private static final long serialVersionUID = 1L;
}
