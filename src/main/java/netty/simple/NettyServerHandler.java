package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author yhw
 * @version 1.0
 **/
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    //具体业务具体写

    //读取数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接收客户端传来的消息，先放到ByteBuf里面
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println(byteBuf.toString());

        System.out.println("客户端地址+："+ctx.channel().remoteAddress());
    }

    //数据读取完毕后的操作
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端~", CharsetUtil.UTF_8));
    }

    //处理异常，需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();

    }
}
