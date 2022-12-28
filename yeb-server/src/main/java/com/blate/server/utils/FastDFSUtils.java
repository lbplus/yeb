package com.blate.server.utils;


import org.apache.poi.util.IOUtils;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @program: yebt
 * @description: FasDFS工具类
 * @author: Honors
 * @create: 2021-08-03 10:32
 */
public class FastDFSUtils {

    private static Logger logger = LoggerFactory.getLogger(FastDFSUtils.class);

    //初始化客户端
    static {
        try {
            // String filePath = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
            // ClientGlobal.init(filePath);
            ClassPathResource classPathResource = new ClassPathResource("fdfs_client.conf");
            // 创建临时文件，将fdfs_client.conf的值赋值到临时文件中
            String tempPath = System.getProperty("java.io.tmpdir") + System.currentTimeMillis() +  ".conf";
            File f = new File(tempPath);
            // 流之间的内容复制
            IOUtils.copy(classPathResource.getInputStream(), new FileOutputStream(f));
            ClientGlobal.init(f.getAbsolutePath());
        } catch (Exception e) {
           logger.error("初始化FastDFS失败",e);
        }
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    public static String[] upload(MultipartFile file){
        String name = file.getOriginalFilename();
        logger.info("文件名:",name);
        StorageClient storageClient = null;
        String[] uploadResults = null;
        try {
            //获取storage客户端
            storageClient = getStorageClient();
            //上传
            uploadResults = storageClient.upload_file(file.getBytes(),
                    name.substring(name.lastIndexOf(".")+1),
                        null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (uploadResults == null){
            logger.error("上传失败",storageClient.getErrorCode());
            System.out.println(storageClient.getErrorCode());
        }
        return uploadResults;
    }

    /**
     * 获取文件信息
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName){
        StorageClient storageClient = null;
        try {
            storageClient = getStorageClient();
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (Exception e) {
            logger.error("文件信息获取失败",e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static InputStream downFile(String groupName, String remoteFileName){
        StorageClient storageClient = null;
        try {
            storageClient = getStorageClient();
            byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileByte);
            return inputStream;
        } catch (Exception e) {
            logger.error("文件下载失败",e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除文件
     * @param groupName
     * @param remoteFileName
     */
    public static void deleteFile(String groupName,String remoteFileName){
        StorageClient storageClient = null;
        try {
            storageClient = getStorageClient();
            storageClient.delete_file(groupName,remoteFileName);
        } catch (Exception e) {
            logger.error("文件删除失败",e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 生成 Storage 客户端
     * @return
     * @throws IOException
     */
    private static StorageClient getStorageClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }

    /**
     * 生成 Tracker 服务器
     * @return
     * @throws IOException
     */
    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        return trackerServer;
    }

    /**
     * 获取文件路径
     * @return
     */
    public static String getTrackerUrl(){
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        StorageServer storeStorage = null;
        try {
            trackerServer = trackerClient.getTrackerServer();
            storeStorage = trackerClient.getStoreStorage(trackerServer);
        } catch (Exception e) {
          logger.error("文件路径获取失败",e.getMessage());
            e.printStackTrace();
        }
        return "http://" + storeStorage.getInetSocketAddress().getHostString() + ":5236/";
    }

}