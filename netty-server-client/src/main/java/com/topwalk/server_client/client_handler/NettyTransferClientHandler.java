package com.topwalk.server_client.client_handler;

import com.topwalk.core.model.BaseModel;
import com.topwalk.core.model.SecureModel;
import com.topwalk.core.model.enums.DataSourceEnum;
import com.topwalk.core.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.Map;

public class NettyTransferClientHandler extends ChannelInboundHandlerAdapter {
	private Logger log = Logger.getLogger(NettyTransferClientHandler.class);

	private String ultimateIp;
	private int ultimatePort;
	private String originIp;
	private int originPort;


	public NettyTransferClientHandler(String originIp,int originPort,String ultimateIp,int ultimatePort) {
		this.ultimateIp = ultimateIp;
		this.ultimatePort = ultimatePort;
		this.originIp = originIp;
		this.originPort = originPort;
	}

	public void channelActive(ChannelHandlerContext ctx) {
		try {
			SecureModel secure = new SecureModel();
			secure.setClientAddr(ctx.channel().localAddress().toString().substring(1));
			secure.setToken("topwalk");  //服务端定义的密钥
            secure.setOriginIp(originIp);
            secure.setOriginPort(originPort);
			secure.setUltimateIp(ultimateIp);
			secure.setUltimatePort(ultimatePort);
			secure.setDataSource(DataSourceEnum.SERVER_CLIENT);
			ctx.writeAndFlush(secure);
		}catch (Exception e){
			log.error("获取主机地址失败...",e);
		}

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		BaseModel baseModel = (BaseModel)msg;
		Channel channel = (Channel) SessionUtil.getServerChannelMap().get(baseModel.getOriginIp()+":"+baseModel.getOriginPort());
		if(channel != null){
			channel.writeAndFlush(msg);
		}
		return;
	}


	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		SessionUtil.unBindClientSession(ctx.channel());
		ctx.close();
	}

}
