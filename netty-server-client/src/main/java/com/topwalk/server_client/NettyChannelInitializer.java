package com.topwalk.server_client;


import com.topwalk.core.code.NettyMessageDecoder;
import com.topwalk.core.code.NettyMessageEncoder;
import com.topwalk.server_client.server_handler.SecureServerHandler;
import com.topwalk.server_client.server_handler.TransferServerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyChannelInitializer extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast(new ObjectEncoder());
		ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
		
		ch.pipeline().addLast(new NettyMessageDecoder());//设置服务器端的编码和解码
		ch.pipeline().addLast(new NettyMessageEncoder());
        
		ch.pipeline().addLast(new SecureServerHandler());
		ch.pipeline().addLast(new TransferServerHandler());
	}

}
