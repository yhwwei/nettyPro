package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author yhw
 * @version 1.0
 **/
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        //boss负责接收客户端连接请求 具体事务请求交给work来
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //服务器启动对象
        ServerBootstrap bootstrap = new ServerBootstrap();

        //配置信息
        try {
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)            //作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,128) //设置线程队列得到的连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true)  //设置保持活动的连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //给workerGrouop的EventLoop对应的管道设置处理器
                        //就是自定义事务处理？？
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("--------服务器starting-------");
            //启动服务器   绑定端口
            ChannelFuture channelFuture = bootstrap.bind(6686).sync();

            //对  关闭通道  监听
            //只有  要关闭channel的消息or事件才会调用这个逻辑
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
        }
    }
}
