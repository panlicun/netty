package com.topwalk.server_client.server_handler;

import com.topwalk.core.model.BaseModel;
import com.topwalk.core.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对客户端的上传数据 有效性验证
 *
 * @author plc
 */
public class TransferServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(TransferServerHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BaseModel baseModel = (BaseModel) msg;
        Channel channel = (Channel) SessionUtil.getClientChannelMap().get(baseModel.getUltimateIp()+":"+baseModel.getUltimatePort());
        if(channel != null){
            channel.writeAndFlush(msg);
        }
        return;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}
