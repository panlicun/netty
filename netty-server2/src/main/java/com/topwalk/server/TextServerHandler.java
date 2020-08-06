package com.topwalk.server;

import com.topwalk.core.model.TextModel;
import com.topwalk.core.session.Session;
import com.topwalk.core.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对客户端的上传数据 有效性验证
 *
 * @author plc
 */
public class TextServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(TextServerHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TextModel) {
            TextModel textModel = (TextModel) msg;
            Session session = SessionUtil.getSession(ctx.channel());
            log.info("接收客户端"+session.getNodeId()+"内容：" + textModel.getMessage());
            textModel.setRecvieSuccess(true);
            ctx.writeAndFlush(textModel);
//            ctx.close();
        } else {
            ctx.fireChannelRead(msg);   //继续执行
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}
