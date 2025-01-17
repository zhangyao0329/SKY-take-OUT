package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Api(tags = "通用接口")
@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
//            原始文件名
            String originalFilename = file.getOriginalFilename();
//            截取原始文件名的后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            构建文件的名称,即在oss中的名字
            String objectName = UUID.randomUUID().toString() + extension;
//            返回访问的网址
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(filePath);

        } catch (IOException e) {
            log.error("文件上传失败: {}", e);
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
