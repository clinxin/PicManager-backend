package com.clinxin.picmanagerbackend.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.clinxin.picmanagerbackend.config.CosClientConfig;
import com.clinxin.picmanagerbackend.exception.BusinessException;
import com.clinxin.picmanagerbackend.exception.ErrorCode;
import com.clinxin.picmanagerbackend.manager.CosManager;
import com.clinxin.picmanagerbackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 图片上传模板
 */
@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param intpuSource      文件
     * @param uploadPathPrefix 上传路径前缀
     * @return 上传结果
     */
    public UploadPictureResult uploadPicture(Object intpuSource, String uploadPathPrefix) {
        // 1. 校验图片
        validPicture(intpuSource);
        // 2. 图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginFilename(intpuSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        // 解析结果并返回
        File file = null;
        try {
            // 3. 创建临时上传文件，获取文件到服务器
            file = File.createTempFile(uploadPath, null);
            // 处理文件来源
            processFile(intpuSource, file);
            // 4. 上传图片到对象存储
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 5. 获取图片信息对象，封装返回结果
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            /* --- 新增缩略图处理 start --- */
            // 获取到图片处理结果
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                // 获取压缩之后得到的文件信息
                CIObject compressedCiObject = objectList.get(0);
                // 缩略图默认等于压缩图
                CIObject thumbnailCiObject = compressedCiObject;
                if (objectList.size() > 1) {
                    thumbnailCiObject = objectList.get(1);
                }
                // 封装压缩图的返回结果
                return buildResult(originalFilename, compressedCiObject, thumbnailCiObject, imageInfo); // imageInfo 图片信息
            }
            /* --- 新增缩略图处理 end --- */
            return buildResult(originalFilename, file, uploadPath, imageInfo);
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 临时文件清理
            deleteTempFile(file);
        }
    }

    /**
     * 封装返回结果
     * 新增缩略图返回结果
     *
     * @param originalFilename  原始文件名
     * @param compressdCiObject 压缩后的对象
     * @param thumbnailCiObject 缩略图对象
     * @param imageInfo         图片信息（属性、颜色搜索）
     * @return
     */
    private UploadPictureResult buildResult(String originalFilename, CIObject compressdCiObject,
                                            CIObject thumbnailCiObject, ImageInfo imageInfo) {
        // 封装返回结果
        int picWidth = compressdCiObject.getWidth();
        int picHeight = compressdCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        // 设置压缩后的原图地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressdCiObject.getKey());
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(compressdCiObject.getSize().longValue());
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressdCiObject.getFormat());
        uploadPictureResult.setPicColor(imageInfo.getAve()); // 颜色搜索
        // 设置缩略图地址
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        // 返回可访问地址
        return uploadPictureResult;
    }

    /**
     * 封装返回结果
     */
    private UploadPictureResult buildResult(String originalFilename, File file, String uploadPath, ImageInfo imageInfo) {
        // 封装返回结果
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicColor(imageInfo.getAve()); // 颜色搜索
        // 返回可访问地址
        return uploadPictureResult;
    }

    /**
     * 校验输入源（本地文件或 URL）
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取输入源的原始文件名
     */
    protected abstract String getOriginFilename(Object inputSource);

    /**
     * 处理输入源并生成本地临时文件
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;


    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }

}
