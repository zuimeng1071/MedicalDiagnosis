package com.ai.medical_diagnosis.controller.common;

import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/common/file")
@Tag(name = "通用接口")
public class FileController {

    private final CommonService commonService;

    public FileController(CommonService commonService) {
        this.commonService = commonService;
    }

    @PostMapping(value = "/upload/image")
    @Operation(summary = "上传图片接口")
    public Result<String> uploadImage(
            @RequestPart("image") MultipartFile image
    ) {
        try {
            // 校验文件是否为空
            if (image.isEmpty()) {
                return Result.error("上传的图像不能为空");
            }

            // 校验是否为图像类型（可选）
            if (!Objects.requireNonNull(image.getContentType()).startsWith("image/")) {
                return Result.error("请上传有效的图像文件");
            }

            // 转为字节数组，传给 Service
            byte[] imageData = image.getBytes();
            return commonService.uploadImage(imageData);

        } catch (Exception e) {
            return Result.error("图像上传失败: " + e.getMessage());
        }
    }

}

