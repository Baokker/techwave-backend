package com.techwave.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.techwave.entity.OssAuth;
import com.techwave.mapper.OssAuthMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @program: TechWave
 * @description: service for images
 * @packagename: com.techwave.utils
 * @author: peng peng
 * @date: 2022-12-04 11:32
 **/
@Service
public class OssService {

    private final OssAuth ossAuth;

    public OssService(OssAuthMapper ossAuthMapper) {
        QueryWrapper<OssAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", 1);

        this.ossAuth = ossAuthMapper.selectOne(queryWrapper);
    }

    public String uploadFile(MultipartFile file)
    {
        String endpoint = ossAuth.getEndpoint();
        String accessKeyId = ossAuth.getKeyId();
        String accessKeySecret = ossAuth.getKeySecret();
        String bucketName = ossAuth.getBucketName();

        InputStream inputStream = null;

        try {
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            // get input stream of file
            inputStream = file.getInputStream();

            // get name of file
            String fileName = file.getOriginalFilename();

            // make the name of file unique
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            fileName = uuid + "-" + fileName;

            // sort by date
            String datePath = String.valueOf(LocalDate.now());

            fileName = datePath + "/" + fileName;

//            System.out.println("fileName : " + fileName);

            ossClient.putObject(bucketName, fileName, inputStream);

            // shut down the connection
            ossClient.shutdown();

            return "https://" + bucketName + "." + endpoint + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String uploadFiles(List<MultipartFile> files)
    {
        String endpoint = ossAuth.getEndpoint();
        String accessKeyId = ossAuth.getKeyId();
        String accessKeySecret = ossAuth.getKeySecret();
        String bucketName = ossAuth.getBucketName();

        List<String> urls = new ArrayList<>();

        OSS ossClient = null;

        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            for (MultipartFile file : files) {
                InputStream inputStream = file.getInputStream();
                String fileName = file.getOriginalFilename();
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                fileName = uuid + "-" + fileName;
                String datePath = String.valueOf(LocalDate.now());
                fileName = datePath + "/" + fileName;

                ossClient.putObject(bucketName, fileName, inputStream);
                urls.add("https://" + bucketName + "." + endpoint + "/" + fileName);
            }

            return String.join(",", urls);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            // Remember to shut down the connection
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


}
