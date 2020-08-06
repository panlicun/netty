# netty
netty搭建管道传输，从client --> server端


A -> B -> B -> C

netty-core 核心模块，主要存放一些model,和一些公用的类
netty-client  客户端，发送端，也就是上面的A
netty-server 服务端，接收端，也就是上面的C
netty-server-client  通道，也就是上面的B

netty-server-client 与 netty-server-client2 一模一样，只不过方便测试，多复制了一份，使用时，将netty-server-client打成jar包，分开部署即可

如果普通使用的话可以直接使用A 到 C 进行传输，上述传递方式有特殊的使用场景，次例子是按上述场景开发的


netty-client.properties 为netty-client的配置文件，为了满足项目需求，采用了外部配置文件的方式
netty-server.properties 为netty-server的配置文件
netty-server-client.properties 为netty-server-client的配置文件

可以在配置文件中设置项目的运行地址，在地址中会生成相应的log日志。可根据代码自行修改
