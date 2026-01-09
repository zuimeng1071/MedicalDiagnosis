package com.ai.medical_diagnosis.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

@Component
public class ImageUtils {

    private final AliOssUtil aliOssUtil;

    public ImageUtils(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    public String ImageBase64ToOssUrl(String base64) {
        // 1. 将base64转换为图片
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        // 1.1 生成文件名
        String fileName = UUID.randomUUID() + ".png";
        // 2. 上传图片到OSS
        return aliOssUtil.upload(imageBytes, fileName);
    }

    public String ImageBase64ToOssUrl(String base64, String fileName) {
        // 1. 将base64转换为图片
        byte[] imageBytes = Base64.getDecoder().decode(base64);
        // 2. 上传图片到OSS
        return aliOssUtil.upload(imageBytes, fileName);
    }

    public String ImageToOssUrl(byte[] image) {
        String fileName = UUID.randomUUID() + ".png";
        return aliOssUtil.upload(image, fileName);
    }

    public String ImageToOssUrl(byte[] image, String fileName) {
        return aliOssUtil.upload(image, fileName);
    }

    /**
     * 压缩图像，使其适合 AI 模型输入（如限制 129024 字节）
     *
     * @param image 原始图像字节数组
     * @param maxSizeInBytes 最大允许字节数（如 129024）
     * @return 压缩后的图像字节数组
     */
    public static byte[] compress(byte[] image, int maxSizeInBytes) throws IOException {
        if (image == null || image.length == 0) {
            throw new IllegalArgumentException("图像数据不能为空");
        }

        // 尝试解码图像
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IllegalArgumentException("无法识别的图像格式");
        }

        // 初始压缩参数
        double quality = 0.7; // 初始质量 70%
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // 如果原图太大，先缩小尺寸
        int targetWidth = Math.min(800, width); // 最大宽度 800px
        int targetHeight = Math.min(800, height);
        targetHeight = (int) (((double) targetWidth / width) * height); // 保持比例

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        // 多次尝试压缩，直到满足大小要求
        while (quality >= 0.3) { // 最低质量 30%
            output.reset();

            Thumbnails.of(originalImage)
                    .size(targetWidth, targetHeight)
                    .outputFormat("jpg")
                    .outputQuality(quality)
                    .toOutputStream(output);

            byte[] result = output.toByteArray();

            if (result.length <= maxSizeInBytes) {
                return result; // 满足条件，返回
            }

            // 否则继续缩小尺寸或降低质量
            quality -= 0.1; // 每次降低 10% 质量
        }

        // 如果仍不满足，强制最小尺寸 + 最低质量
        output.reset();
        Thumbnails.of(originalImage)
                .size(512, 512)
                .outputFormat("jpg")
                .outputQuality(0.3)
                .toOutputStream(output);

        byte[] fallback = output.toByteArray();
        if (fallback.length > maxSizeInBytes) {
            throw new RuntimeException("即使最小压缩也无法满足大小限制，请检查图像内容");
        }

        return fallback;
    }

    // 默认调用：限制为 129024 字节（常见 AI 模型限制）
    public static byte[] compress(byte[] image) {
        try {
            return compress(image, 5242880);
        } catch (IOException e) {
            throw new RuntimeException("图像压缩失败", e);
        }
    }
    /**
     * 将图片URL转换为Base64编码
     *
     * @param imgUrl 图片URL
     * @return Base64编码的字符串
     * @throws Exception 如果图片URL无法访问或转换失败
     */

    public String urlToBase64(String imgUrl) throws Exception {
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            byte[] imageBytes = out.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } finally {
            conn.disconnect();
        }
    }

    /**
     * 使用HttpClient下载图片
     *
     * @param imageUrl 图片URL
     * @return 图片字节数组
     * @throws IOException 如果下载失败
     */
    public byte[] downloadWithHttpClient(String imageUrl) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(imageUrl);
        request.setConfig(org.apache.http.client.config.RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(10000)
                .build());

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toByteArray(response.getEntity());
            } else {
                throw new IOException("HTTP " + response.getStatusLine().getStatusCode());
            }
        }
    }
}
