package com.topwalk;

import com.topwalk.client.NettyTransferClient;



/**
 * 负责推送离线消息
 * @author longyingan
 *
 */
public class PushTask implements QueueTask {

	
	
	private Integer messageIdStart;
	
	public PushTask(Integer messageIdStart){
		this.messageIdStart = messageIdStart;
	}

	@Override
	public void executeTask() {
		NettyTransferClient.main(new String[]{String.valueOf(messageIdStart)});
	}

	

}
