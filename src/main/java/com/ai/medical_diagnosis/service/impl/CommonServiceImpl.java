package com.ai.medical_diagnosis.service.impl;

import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.CommonService;
import com.ai.medical_diagnosis.utils.AliOssUtil;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class CommonServiceImpl implements CommonService {

    private final AliOssUtil aliOssUtil;

    public CommonServiceImpl(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    /**
     * 上传图片
     * @param image 上传的图片
     * @return 图片的URL
     */
    @Override
    public Result<String> uploadImage(byte[] image) {
        if (image == null || image.length == 0) {
            return Result.error("图片内容为空");
        }

        // 1. 根据字节内容判断图片格式
        String extension = getExtensionByImageBytes(image);
        if (extension == null) {
            return Result.error("不支持的图片格式");
        }

        // 2. 构造带后缀的 objectName
        String objectName = "images/" + UUID.randomUUID() + extension; // 如: images/abc123.jpg

        // 3. 上传到 OSS
        String url = aliOssUtil.upload(image, objectName);

        return Result.success(url);
    }

    /**
     * 根据图片字节数组判断文件扩展名
     */
    private String getExtensionByImageBytes(byte[] bytes) {
        if (bytes.length < 4) {
            return null;
        }

        // JPEG: FF D8 FF
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
            return ".jpg";
        }

        // PNG: 89 50 4E 47
        if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
            return ".png";
        }

        // GIF: 47 49 46 38
        if (bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x38) {
            return ".gif";
        }

        // BMP: 42 4D
        if (bytes[0] == 0x42 && bytes[1] == 0x4D) {
            return ".bmp";
        }

        // WebP: RIFF .... WEBP
        if (bytes.length >= 12 &&
                bytes[0] == 0x52 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x46 && // "RIFF"
                bytes[8] == 0x57 && bytes[9] == 0x45 && bytes[10] == 0x42 && bytes[11] == 0x50) { // "WEBP"
            return ".webp";
        }

        return null; // 不支持的格式
    }
}