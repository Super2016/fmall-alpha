package cn.fmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理服务
 */
public interface IFileService {

    //文件上传
    public String uploadFile(MultipartFile file, String path);

}
