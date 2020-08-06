package com.topwalk.server_client2;

import com.topwalk.core.code.NettyMessageDecoder;
import com.topwalk.core.code.NettyMessageEncoder;
import com.topwalk.core.model.enums.DataSourceEnum;
import com.topwalk.core.session.Session;
import com.topwalk.core.util.SessionUtil;
import com.topwalk.server_client2.client_handler.NettyTransferClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NettyClientThread extends Thread{

	private static final Logger log = LoggerFactory.getLogger(NettyClientThread.class);

	private int server_port;
	private String server_host;
	private int localPort;
	private String originIp;
	private int originPort;
	private String ultimateIp;
	private int ultimatePort;

	private DataSourceEnum dataSource;


	public NettyClientThread(int server_port, String server_host, int localPort, String originIp, int originPort, String ultimateIp, int ultimatePort, DataSourceEnum dataSource) {
		this.server_port = server_port;
		this.server_host = server_host;
		this.localPort = localPort;
		this.originIp = originIp;
		this.originPort = originPort;
		this.ultimateIp = ultimateIp;
		this.ultimatePort = ultimatePort;
		this.dataSource = dataSource;
	}

	public void run() {
		try {
			connectServerClient(server_port,server_host,localPort,originIp,originPort,ultimateIp,ultimatePort);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 以客户端的身份连接其他服务端
	 * @param server_port  服务端端口
	 * @param server_host  服务端IP
	 * @param localPort    本地客户端启动端口
	 * @param ultimateIp   最终传输的IP地址
	 * @param ultimatePort  最终传输端口
	 * @throws Exception
	 */
	private void connectServerClient(int server_port, String server_host,int localPort,String originIp,int originPort, String ultimateIp,int ultimatePort) throws Exception {
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
					ch.pipeline().addLast(new NettyTransferClientHandler(originIp,originPort,ultimateIp,ultimatePort));
				}
			});
			ChannelFuture f = b.connect(server_host,server_port).sync();
			if (f.isSuccess()) {
				log.info(server_host + "/" + server_port + "---连接服务器成功...");
				Channel channel = f.channel();
				//管理连接
                SessionUtil.bindClientSession(new Session(ultimateIp+":"+ultimatePort), channel);
			} else {
				log.info("连接server-client服务器失败");
			}
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
	

}
