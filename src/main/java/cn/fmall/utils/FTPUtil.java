package cn.fmall.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * FTP工具类
 */
public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPwd = PropertiesUtil.getProperty("ftp.password");

    private String ip;
    private String username;
    private String password;
    private int port;
    private FTPClient ftpClient;

    //构造器
    public FTPUtil(String ip,int port,String username,String password){
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    //开放方法
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPwd);
        logger.info("开始连接FTP服务器");
        //调用uploadFile,连接并上传文件
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("上传过程结束,结果>>{}",result);
        //返回结果成功与否
        return result;
    }

    /**
     * 具体文件上传处理逻辑
     * @param remotePath
     * @param fileList
     * @return
     * @throws IOException
     */
    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fileInputStream = null;
        //调用connectServer,连接FTP服务器
        if (connectServer(this.ip,this.port,this.username,this.password)) {
            try {
                //是否切换工作目录
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                for (File fileItem:fileList) {
                    fileInputStream = new FileInputStream(fileItem);
                    //存储文件
                    ftpClient.storeFile(fileItem.getName(),fileInputStream);
                }
            } catch (IOException e) {
                logger.error("文件上传异常>>>",e);
                uploaded = false;
            } finally {
                fileInputStream.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    /**
     * 连接FTP服务器逻辑
     * @param ip
     * @param port
     * @param username
     * @param password
     * @return
     */
    private boolean connectServer(String ip,int port,String username, String password){
        boolean connectionIsSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            connectionIsSuccess = ftpClient.login(username,password);

        } catch (IOException e) {
            logger.error("FTP服务器连接异常>>>",e);
        }
        return connectionIsSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
