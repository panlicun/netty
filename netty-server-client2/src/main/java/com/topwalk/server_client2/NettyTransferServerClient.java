package com.topwalk.server_client2;


import com.topwalk.core.util.FileTransferProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.core.io.FileSystemResourceLoader;

import java.io.IOException;

public class NettyTransferServerClient {

    private Logger log = Logger.getLogger(NettyTransferServerClient.class);

    public void bind(int localServerPort) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new NettyChannelInitializer());

            log.info("服务端启动成功，bind port:" + localServerPort);
            ChannelFuture f = b.bind(localServerPort).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        init();
        // 获取端口
        int localServerPort = FileTransferProperties.getInt("local_server_port", 10013);

        try {
            new NettyTransferServerClient().bind(localServerPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static void init() {
        try {
            //请把加载属性文件放在 加载日志配置的上面，因为读取日志输出的目录配置在 属性文件里面
            FileTransferProperties.load("E:\\code\\netty-file-3rd\\netty-server-client2.properties");

            System.setProperty("WORKDIR", FileTransferProperties.getString("work_dir", "/"));

            PropertyConfigurator.configure(new FileSystemResourceLoader().getResource(
                    "classpath:log4j.xml").getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
