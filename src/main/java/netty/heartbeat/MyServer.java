package netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @version 1.0
 * @decription 测试心跳机制
 **/
public class MyServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))   //bossGroup 使用netty自带的日志handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            /*
                            * 心跳机制
                            * ①即当在指定的时间间隔内没有从 Channel 读取到数据时
                            * ②即当在指定的时间间隔内没有数据写入到 Channel 时
                            * ③多久没有读写事件了
                            * 会生成一个IdleEvent  交给pipeline中下一个handler处理（于是我们可以自定义）
                            * */
                            pipeline.addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS));
                            // 解码和编码，应和客户端一致
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            //处理IdleEvent
                            pipeline.addLast(new ServerHeartBeatHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(9999).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }
}
