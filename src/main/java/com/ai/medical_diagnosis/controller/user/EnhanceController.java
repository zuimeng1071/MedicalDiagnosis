package com.ai.medical_diagnosis.controller.user;


import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.EnhanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图像增强接口
 * 暂不使用
 */
//@RestController
//@RequestMapping("/user/enhance")
@Tag(name = "图像增强接口")
public class EnhanceController {

    private final EnhanceService enhanceService;

    public EnhanceController(EnhanceService enhanceService) {
        this.enhanceService = enhanceService;
    }

    @PostMapping(value = "/basic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "图像基本增强接口")
    public Result<String> basic(@RequestParam("image") MultipartFile image) {
        try {
            byte[] imageData = image.getBytes();
            return Result.success(enhanceService.basic(imageData));
        }
        catch (Exception e){
            return Result.error("图像处理失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/pca", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "图像PCA增强接口")
    public Result<String> pca(@RequestParam("image") MultipartFile image) {
        try {
            byte[] imageData = image.getBytes();
            return Result.success(enhanceService.pca(imageData));
        }
        catch (Exception e){
            return Result.error("图像处理失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/applyAll", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "图像所有增强接口")
    public Result<String> applyAll(@RequestParam("image") MultipartFile image) {
        try {
            byte[] imageData = image.getBytes();
            return Result.success(enhanceService.applyAll(imageData));
        }
        catch (Exception e){
            return Result.error("图像处理失败: " + e.getMessage());
        }
    }
}
