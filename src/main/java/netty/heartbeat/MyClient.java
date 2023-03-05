package netty.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @version 1.0
 **/
public class MyClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap client = new Bootstrap();

        EventLoopGroup group = new NioEventLoopGroup();

        client.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        //客户端channel4秒内没有发送写事件，则往服务器发送一个心跳，表示自己客户端虽然没有传送数据到服务器，但是自己还活着
                        //这个writeIdleTime要比服务器那边的readerIdleTime要小
                        pipeline.addLast(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));

                        // 解码和编码，应和服务端一致
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());

                        //处理客户端的业务逻辑
                        pipeline.addLast(new ClientHeartBeatChannel());
                    }
                });
        ChannelFuture channelFuture = client.connect("127.0.0.1", 9999).sync();
        //给服务端发送数据
        String str = "Hello Netty";
        channelFuture.channel().writeAndFlush(str);
        System.out.println("客户端发送数据:" + str);
    }

}

