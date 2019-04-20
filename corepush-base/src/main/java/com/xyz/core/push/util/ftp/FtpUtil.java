package com.xyz.core.push.util.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import lombok.extern.slf4j.Slf4j;

/**
 * Ftp工具类
 */
@Slf4j
public class FtpUtil {

    /**
     * 上传文件到ftp服务器
     *
     * @param fileName       文件名
     * @param localFilePath  本地文件路径(以"/"结尾)
     * @param uploadFilePath 服务器文件路径
     * @param config         存放了ftp相关的配置
     * @return
     */
    public static boolean uploadFile(String fileName, String localFilePath, String uploadFilePath, FtpConfig config) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(config.getTimeout());
        boolean result = false;
        try {
            ftpClient.connect(config.getHost(), config.getPort());
            ftpClient.login(config.getUsername(), config.getPassword());
            if (!ftpClient.changeWorkingDirectory(uploadFilePath)) {
                if (ftpClient.makeDirectory(uploadFilePath)) {
                    ftpClient.changeWorkingDirectory(uploadFilePath);
                } else {
                    log.error("========= ftp创建文件路径失败 ========");
                    return result;
                }
            }
            ftpClient.enterLocalPassiveMode();//开启被动模式，客户端传东西到服务端
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);//开启二进制传输，防止图片损坏
            FileInputStream fis = new FileInputStream(localFilePath + fileName);
            try {
                if (ftpClient.storeFile(fileName, fis)) {
                    result = true;
                }
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        } catch (IOException e) {
            log.error("文件上传失败", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("ftpClient断连失败", e);
            }
        }
        return result;
    }


    /**
     * 从ftp服务器下载文件
     *
     * @param fileName         文件名
     * @param localFilePath    本地文件路径
     * @param downloadFilePath 服务器文件路径
     * @param config           存放了ftp相关的配置
     * @return
     */
    public static boolean downloadFile(String fileName, String localFilePath, String downloadFilePath, FtpConfig config) {
        boolean result = false;
        FTPClient ftpClient = new FTPClient();
        ftpClient.setConnectTimeout(config.getTimeout());
        try {
            ftpClient.connect(config.getHost(), config.getPort());
            ftpClient.login(config.getUsername(), config.getPassword());
            ftpClient.changeWorkingDirectory(downloadFilePath);

            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile ftpFile : files) {
                if (ftpFile.getName().equals(fileName)) {
                    OutputStream os = null;
                    File dir = new File(localFilePath);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(localFilePath + fileName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try {
                        os = new FileOutputStream(file);
                        ftpClient.retrieveFile(fileName, os);
                    } finally {
                        if (os != null) {
                            os.close();
                        }
                    }
                }
            }
            ftpClient.logout();
            result = true;
        } catch (IOException e) {
            log.error("文件下载失败", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("ftpClient断连失败", e);
            }
        }
        return result;
    }

}
