package cn.fmall.service.imp;

import cn.fmall.common.ServerResponse;
import cn.fmall.service.IFileService;
import cn.fmall.utils.FTPUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService{

    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 文件上传
     * @param file
     * @param path
     * @return
     */
    @Override
    public String uploadFile(MultipartFile file,String path){
        //取得原始文件名
        String originalFileName = file.getOriginalFilename();

        //获取扩展名,以最后一个"."的位置开始获取字符(查询实际从后向前的方式查起)
        String fileNameExt = originalFileName.substring(originalFileName.lastIndexOf("."));
        //截取除开后缀的文件名称
        String pref = originalFileName.substring(0,originalFileName.lastIndexOf("."));
        //生成最后存储的文件名,使用UUID
        String fileNameUUID = pref+UUID.randomUUID().toString()+fileNameExt;
        //logback使用{}占位符
        logger.info("文件上传开始>>原文件名:{},上传路径:{},新文件名:{}",originalFileName,path,fileNameUUID);
        //确定目录名
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            //给可写权限
            fileDir.setWritable(true);
            //创建文件夹
            fileDir.mkdirs();
        }
        //目标文件
        File targetFile = new File(path,fileNameUUID);

        try {
            //将接收到的文件传输到指定的目标文件
            file.transferTo(targetFile);
            //将tartgetFile上传到FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //传完成删除本地tomcat下upload文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("文件上传失败:"+e);
            return null;
        }
        //返回目标文件名
        return targetFile.getName();
    }

}
