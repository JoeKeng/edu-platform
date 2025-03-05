package com.edusystem.util;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class AliyunOssUtil {

//    @Value("${aliyun.oss.endpoint}")
//    private String endpoint;
//
//    @Value("${aliyun.oss.bucketName}")
//    private String bucketName;
//
//    @Value("${aliyun.oss.region:cn-hangzhou}") // 默认 cn-hangzhou
//    private String region;

    private final String endpoint;
    private final String bucketName;
    private final String region;

    @Autowired
    public AliyunOssUtil(AliyunOssProperties aliyunOssProperties) {
        this.endpoint = aliyunOssProperties.getEndpoint();
        this.bucketName = aliyunOssProperties.getBucketName();
        this.region = aliyunOssProperties.getRegion();
    }
    /**
     * 生成存储路径：{type}/yyyy/MM/{UUID}.ext
     */
    private String generateFilePath(String folder, String originalFilename) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        return folder + "/" + datePath + "/" + newFileName;
    }

    /**
     * 创建 OSS 客户端（仅使用环境变量）
     */
    private OSS createOssClient() throws ClientException {
//        // 使用环境变量
//        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
//        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
//        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
//        return OSSClientBuilder.create()
//                .endpoint(endpoint)
//                .credentialsProvider(credentialsProvider)
//                .clientConfiguration(clientBuilderConfiguration) // 适用于国际站
//                .region(region)
//                .build();

        // 确保环境变量已正确配置
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        // 创建 OSS 客户端配置
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();

        // 仅当国际站（非中国区域）时，才设置 SignVersion.V4
        if (endpoint.contains("oss-us-") || endpoint.contains("oss-eu-") || endpoint.contains("oss-ap-")) {
            clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        }
        // 创建 OSS 客户端
        return OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

    }

    /**
     * 上传 MultipartFile 文件
     */
    public String uploadFile(MultipartFile file, String folder) {
        if (file.isEmpty()) return null;
        String filePath = generateFilePath(folder, file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            OSS ossClient = createOssClient();
            ossClient.putObject(bucketName, filePath, inputStream);
            ossClient.shutdown();
            return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传 byte[] 文件
     */
    public String uploadFile(byte[] content, String originalFilename, String folder) {
        if (content == null || content.length == 0) return null;
        String filePath = generateFilePath(folder, originalFilename);

        try {
            OSS ossClient = createOssClient();
            ossClient.putObject(bucketName, filePath, new ByteArrayInputStream(content));
            ossClient.shutdown();
            return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
