package com.topwalk.server_client.server_handler;

import com.topwalk.core.model.enums.DataSourceEnum;
import com.topwalk.core.session.Session;
import com.topwalk.core.util.FileTransferProperties;
import com.topwalk.core.util.SessionUtil;
import com.topwalk.server_client.NettyClientThread;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.topwalk.core.model.SecureModel;
import org.springframework.util.StringUtils;

/**
 * 对客户端的上传数据 有效性验证
 *
 * @author 洋白菜
 */
public class SecureServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SecureServerHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SecureModel) {
            SecureModel secureModel = (SecureModel) msg;
            if (secureModel.getToken() != null) {
                //TODO  验证 token 是否存在，并且token对应的 ip和 ctx里面来源ip是否一致
                if (secureModel.getToken().equals("topwalk")) {

                    SessionUtil.bindServerSession(new Session(secureModel.getOriginIp()+":"+secureModel.getOriginPort()), ctx.channel());
                    log.info("NEW TCP --> " + ctx.channel().remoteAddress());
                    log.info("now connection count --> " + SessionUtil.getServerChannelMap().size());

                    //验证最终连接地址
                    if (StringUtils.isEmpty(secureModel.getUltimateIp()) || secureModel.getUltimatePort() == 0) {
                        log.info("最终连接地址为空...连接失败");
                        secureModel.setAutoSuccess(false);
                        ctx.writeAndFlush(secureModel);
                        return;
                    }


                    int localClientPort = FileTransferProperties.getInt("local_client_port", 10012);
                    String remoteServerIp;
                    int remoteServerPort;
                    if(secureModel.getDataSource() == DataSourceEnum.CLIENT){
                        remoteServerIp = FileTransferProperties.getString("remote_server_ip", "127.0.0.1");
                        remoteServerPort = FileTransferProperties.getInt("remote_server_port", 10014);
                    }else{
                        remoteServerIp = secureModel.getUltimateIp();
                        remoteServerPort = secureModel.getUltimatePort();
                    }

                    //如果存在该管道就不再重复连接
                    String clientMapKey = secureModel.getUltimateIp() + ":" + secureModel.getUltimatePort();
                    if (null != SessionUtil.getClientChannelMap().get(clientMapKey)) {
                        return;
                    }
                    new NettyClientThread(remoteServerPort, remoteServerIp, localClientPort,secureModel.getOriginIp(),secureModel.getOriginPort(),secureModel.getUltimateIp(),secureModel.getUltimatePort(), secureModel.getDataSource()).start();
                    return;
                }
            }
            secureModel.setAutoSuccess(false);
            ctx.writeAndFlush(secureModel);
            ctx.close();
        } else {
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
