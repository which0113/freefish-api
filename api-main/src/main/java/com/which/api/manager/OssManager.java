package com.which.api.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

import static com.which.api.constant.CommonConstant.BUCKET_NAME;

/**
 * oss对象存储操作
 *
 * @author which
 */
@Slf4j
@Component
public class OssManager {

    @Resource
    private OSS ossClient;

    /**
     * 上传对象
     *
     * @param key
     * @param localFilePath
     * @return
     */
    public void putObject(String key, String localFilePath) {
        try {
            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, new File(localFilePath));
            ossClient.putObject(putObjectRequest);
            log.info("上传文件成功！");
        } catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage());
        }
    }

    /**
     * 上传对象
     *
     * @param key
     * @param file
     * @return
     */
    public void putObject(String key, File file) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, file);
            ossClient.putObject(putObjectRequest);
            log.info("上传文件成功！");
        } catch (Exception e) {
            log.error("上传文件失败：{}", e.getMessage());
        }
    }

}
