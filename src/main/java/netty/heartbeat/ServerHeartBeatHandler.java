package netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author yhw
 * @version 1.0
 **/
public class ServerHeartBeatHandler extends ChannelInboundHandlerAdapter {
    private int idle_count=1;
    private int count=1;
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {


                //服务器发现这个channel里面没有数据要读，也就是客户端没有发送数据到服务器，同时也没有心跳过来
                //两次机会，
                case READER_IDLE:
                    if(idle_count>2){
                        System.out.println("超过两次无客户端请求，关闭该channel");
                        ctx.channel().close();
                    }
                    System.out.println("已等待5秒还没收到客户端发来的消息");
                    idle_count++;
                    break;
                case WRITER_IDLE:
                default:
                    break;
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    //客户端发送数据过来，服务器接收
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("第" + count + "次" + "，服务端收到的消息:" + msg);

        String message = (String) msg;
        // 如果是心跳命令，服务端收到命令后回复一个相同的命令给客户端
        if ("hb_request".equals(message)) {
            ctx.writeAndFlush("服务端成功收到心跳信息");
        }

        count++;
    }
}
