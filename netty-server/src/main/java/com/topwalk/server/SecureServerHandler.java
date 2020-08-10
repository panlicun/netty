package com.topwalk.server;

import com.topwalk.core.session.Session;
import com.topwalk.core.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.topwalk.core.model.SecureModel;

/**
 * 对客户端的上传数据 有效性验证
 * @author 洋白菜
 *
 */
public class SecureServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(SecureServerHandler.class);

    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if( msg  instanceof SecureModel ){
        	SecureModel secureModel = (SecureModel)msg;
        	if(secureModel.getToken() != null ){
        		//TODO  验证 token 是否存在，并且token对应的 ip和 ctx里面来源ip是否一致
        		if(secureModel.getToken().equals("topwalk")){
					SessionUtil.bindServerSession(new Session(secureModel.getOriginIp()+":"+secureModel.getOriginPort()), ctx.channel());
        			log.info("NEW TCP --> " + ctx.channel().remoteAddress());
        			log.info("now connection count --> " +SessionUtil.getServerChannelMap().size());
        			secureModel.setAutoSuccess(true);
                	ctx.writeAndFlush(secureModel);
        			return ;
        		}
        	}
        	secureModel.setAutoSuccess(false);
        	ctx.writeAndFlush(secureModel);
        	ctx.close();
        }else{
			ctx.fireChannelRead(msg);   //继续执行
        }
    }
    
    
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		SessionUtil.unBindServerSession(ctx.channel());
		ctx.close();
	}


}
