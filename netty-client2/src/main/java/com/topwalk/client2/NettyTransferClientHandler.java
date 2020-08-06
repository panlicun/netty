package com.topwalk.client2;

import com.topwalk.core.model.ResponseFile;
import com.topwalk.core.model.SecureModel;
import com.topwalk.core.model.TextModel;
import com.topwalk.core.model.enums.DataSourceEnum;
import com.topwalk.core.util.FileTransferProperties;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.net.InetAddress;

public class NettyTransferClientHandler extends ChannelInboundHandlerAdapter {
	private Logger log = Logger.getLogger(NettyTransferClientHandler.class);

	String ultimateIp = FileTransferProperties.getString("ultimate_ip", "127.0.0.1");
	int ultimatePort = FileTransferProperties.getInt("ultimate_port", 10016);



	public void channelActive(ChannelHandlerContext ctx) {
		try {
			SecureModel secure = new SecureModel();
			secure.setClientAddr(ctx.channel().localAddress().toString().substring(1));
			secure.setToken("topwalk");  //服务端定义的密钥
			secure.setUltimateIp(ultimateIp);
			secure.setUltimatePort(ultimatePort);
			secure.setDataSource(DataSourceEnum.CLIENT);
			ctx.writeAndFlush(secure);
		}catch (Exception e){
			log.error("获取主机地址失败...",e);
		}

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if(msg instanceof SecureModel){
			SecureModel secureModel = (SecureModel)msg;
			if(secureModel.isAutoSuccess()){
				log.info("服务器认证成功，可以进行传输...");
				NettyTransfer.secureStatus = true;
			}
			return ;
		}

		if(msg instanceof TextModel){
			TextModel textModel = (TextModel)msg;
			if(textModel.isRecvieSuccess()){
				log.info("服务端接收消息成功...");
				return;
			}
		}
		
		if (msg instanceof ResponseFile) {
			ResponseFile response = (ResponseFile)msg;
			new NettyTransfer().responseFileHandle(response,NettyTransferClient.channel);

		}
	}


	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}
