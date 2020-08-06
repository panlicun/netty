package com.topwalk.client;

import com.topwalk.core.code.NettyMessageDecoder;
import com.topwalk.core.code.NettyMessageEncoder;
import com.topwalk.core.util.FileTransferProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.core.io.FileSystemResourceLoader;

import java.io.IOException;

public class NettyTransferClient {
    private Logger log = Logger.getLogger(NettyTransferClient.class);
    public static Channel channel = null;

    public void connect(int server_port, String server_host,int localPort) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度

                    ch.pipeline().addLast(new NettyMessageDecoder());//设置服务器端的编码和解码
                    ch.pipeline().addLast(new NettyMessageEncoder());
                    ch.pipeline().addLast(new NettyTransferClientHandler());
                }
            });
            ChannelFuture f = b.localAddress(localPort).connect(server_host,server_port).sync();
            if (f.isSuccess()) {
                log.info(server_host + "/" + server_port + "---连接服务器成功...");
                channel = f.channel();
                Thread.sleep(10000);
                //使用线程测试
                for (int i = 0; i < 1; i++) {//使用循环创建线程
                    try {
                        ClientThread t = new ClientThread();
                        t.start();//使用start()方法，不会阻塞
                        System.out.println("第【" + i + "】个线程启动完成,每20秒增加一个线程");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                log.info("连接服务器失败");
            }
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        init();

        // 获取端口
        String serverIp = FileTransferProperties.getString("server_ip", "127.0.0.1");
        int serverPort = FileTransferProperties.getInt("server_port", 10012);
        int port = FileTransferProperties.getInt("port", 11111);

        try {
            new NettyTransferClient().connect(serverPort, serverIp,port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void init() {
        try {
            //请把加载属性文件放在 加载日志配置的上面，因为读取日志输出的目录配置在 属性文件里面
            FileTransferProperties.load("E:\\code\\netty-file-3rd\\netty-client.properties");

            System.setProperty("WORKDIR", FileTransferProperties.getString("work_dir", "/"));

            PropertyConfigurator.configure(new FileSystemResourceLoader().getResource(
                    "classpath:log4j.xml").getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
