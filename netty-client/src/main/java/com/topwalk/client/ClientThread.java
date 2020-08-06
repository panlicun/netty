package com.topwalk.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;
import java.util.Random;


public class ClientThread extends Thread{

	public void run() {
		try {
			for(int i=0;i<10;i++){
				if(i%2 == 1){
					System.out.println("线程【"+Thread.currentThread().getName()+"】开始处理 : 第【"+ i +"】次循环,发送字符串");
					String msg = getRandomString(1024*2L);
					System.out.println(msg);
					new NettyTransfer().sendMsg(msg,NettyTransferClient.channel);
				}else{
					System.out.println("线程【"+Thread.currentThread().getName()+"】开始处理 : 第【"+ i +"】次循环,发送文件");
					try{
						File file = new File("F:\\迅雷下载\\virtualdj2018.rar");
						new NettyTransfer().sendFile(file,NettyTransferClient.channel);
					}catch (Exception e){
						e.printStackTrace();
					}
			}
				Thread.sleep(2000);
			}
//			File file = new File("F:\\迅雷下载\\virtualdj2018.rar");
//			new NettyTransfer().sendFile(file,NettyTransferClient.channel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
       
	}
	
	public static void main(String[] args) {
		for(int i=0;i<10;i++) {//使用循环创建线程
			try {
				ClientThread t = new ClientThread();
				t.start();//使用start()方法，不会阻塞
				System.out.println("第【"+ i +"】个线程启动完成,每20秒增加一个线程");
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	  }

	public static String getRandomString(long length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(str.length());
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}
}
