package com.topwalk.client2;

import com.topwalk.core.model.RequestFile;
import com.topwalk.core.model.ResponseFile;
import com.topwalk.core.model.TextModel;
import com.topwalk.core.model.enums.DataSourceEnum;
import com.topwalk.core.util.FileTransferProperties;
import com.topwalk.core.util.MD5FileUtil;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class NettyTransfer {

    private Logger log = Logger.getLogger(NettyTransfer.class);

    public static boolean secureStatus = true;
    private int byteRead;

    public int readBufferSize = FileTransferProperties.getInt("readBufferSize", 8192);

    String ultimateIp = FileTransferProperties.getString("ultimate_ip", "127.0.0.1");
    int ultimatePort = FileTransferProperties.getInt("ultimate_port", 10016);


    public void sendMsg(String msg, Channel channel) {
        if (channel == null) {
            return;
        }
        if (!secureStatus) {
            log.info("客户端连接验证失败...");
            return;
        }
        log.info("向服务端发送消息：" + msg);
        TextModel textModel = new TextModel();
        textModel.setMessage(msg);
        textModel.setDataSource(DataSourceEnum.CLIENT);
        textModel.setUltimateIp(ultimateIp);
        textModel.setUltimatePort(ultimatePort);
        channel.writeAndFlush(textModel);
    }

    public void sendFile(File file, Channel channel) throws Exception {
        if (channel == null) {
            return;
        }
        if (!secureStatus) {
            log.info("客户端连接验证失败...");
            return;
        }
        log.info("向服务端发送文件：" + file.getName());
        RandomAccessFile randomAccessFile = null;
        try {
            RequestFile echo = new RequestFile();
            echo.setUltimateIp(ultimateIp);
            echo.setUltimatePort(ultimatePort);
            echo.setDataSource(DataSourceEnum.CLIENT);
            randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(0);
            byte[] bytes = new byte[readBufferSize];
            if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                String fileName = file.getName();// 文件名
                echo.setFile_md5(MD5FileUtil.getFileMD5String(file));
                echo.setFile_name(fileName);
                echo.setFile_type(getSuffix(fileName));
                echo.setStarPos(0);// 文件开始位置
                echo.setFile(file);
                echo.setEndPos(byteRead);
                echo.setBytes(bytes);
                echo.setFile_size(randomAccessFile.length());
                channel.writeAndFlush(echo);
            } else {
                System.out.println("文件已经读完");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }finally {
            randomAccessFile.close();
        }
        return;
    }

    public void responseFileHandle(ResponseFile response, Channel channel) throws Exception{
        System.out.println(response.toString());
        RequestFile rf = response.getRequestFile();
        if(!response.isEnd()){
            long start = response.getStart();
            if (start != -1) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(rf.getFile(), "r");
                randomAccessFile.seek(start);
                int a = (int) (randomAccessFile.length() - start);
                int sendLength = readBufferSize;
                if (a < readBufferSize) {
                    sendLength = a;
                }
                byte[] bytes = new byte[sendLength];
                if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                    rf.setStarPos(start);
                    rf.setEndPos(byteRead);
                    rf.setBytes(bytes);
                    try {
                        channel.writeAndFlush(rf);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    randomAccessFile.close();
//                    FileTransferClient.channel.close();
                }
            }
        }
    }


    private static String getSuffix(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        return fileType;
    }
}
