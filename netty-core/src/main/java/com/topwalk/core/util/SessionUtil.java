package com.topwalk.core.util;

import com.topwalk.core.attribute.Attributes;
import com.topwalk.core.session.Session;
import io.netty.channel.Channel;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SessionUtil {

	/**
	 * 存放的是上一节点链接到当前服务端的channel，key的规则，起始IP：端口
	 */
	private static final Map<String, Channel> SERVER_CHANNEL_MAP = new HashMap<>();

	/**
	 * 存放的是下一节点要链接的服务端的channel,key的规则是，最终IP:端口
	 */
	private static final Map<String, Channel> CLIENT_CHANNEL_MAP = new HashMap<>();


	public static void bindServerSession(Session session, Channel channel) {
		SERVER_CHANNEL_MAP.put(session.getNodeId(), channel);
		channel.attr(Attributes.SESSION).set(session);
	}

	public static void unBindServerSession(Channel channel) {
		if (hasLogin(channel)) {
			Session session = getSession(channel);
			SERVER_CHANNEL_MAP.remove(session.getNodeId());
			channel.attr(Attributes.SESSION).set(null);
			System.out.println(new Date() + " " + session + "退出集群");
		}
	}

	public static void bindClientSession(Session session, Channel channel) {
		CLIENT_CHANNEL_MAP.put(session.getNodeId(), channel);
		channel.attr(Attributes.SESSION).set(session);
	}

	public static void unBindClientSession(Channel channel) {
		if (hasLogin(channel)) {
			Session session = getSession(channel);
			CLIENT_CHANNEL_MAP.remove(session.getNodeId());
			channel.attr(Attributes.SESSION).set(null);
			System.out.println(new Date() + " " + session.getNodeId() + "退出集群");
		}
	}

	private static boolean hasLogin(Channel channel) {
		return channel.hasAttr(Attributes.SESSION);
	}

	public static Session getSession(Channel channel) {
		return channel.attr(Attributes.SESSION).get();
	}

	public static Map getServerChannelMap() {
		return SERVER_CHANNEL_MAP;
	}

	public static Map getClientChannelMap() {
		return CLIENT_CHANNEL_MAP;
	}

}
