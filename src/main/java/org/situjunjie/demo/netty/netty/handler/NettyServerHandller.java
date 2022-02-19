package org.situjunjie.demo.netty.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author situjunjie@foxmail.com
 * @date 2022年02月19日 20:54
 */
public class NettyServerHandller extends ChannelInboundHandlerAdapter {

    /**
     * 收到数据要读出来
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端:"+ctx.channel().remoteAddress()+" 发来消息："+((ByteBuf) msg).toString(StandardCharsets.UTF_8));
        //下面假设处理一个耗时长的业务来联系taskqueue异步处理
        ctx.channel().eventLoop().execute(()->{
            try {
                System.out.println("开始跑一个耗时5秒的任务1   " + System.currentTimeMillis());
                Thread.sleep(10*1000);
                System.out.println("长耗时任务1完成  "+ System.currentTimeMillis());
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        //如果多个任务在taskqueue会排队执行
        ctx.channel().eventLoop().execute(()->{
            try {
                System.out.println("开始跑一个耗时5秒的任务2   " + System.currentTimeMillis());
                Thread.sleep(10*1000);
                System.out.println("长耗时任务2完成  "+ System.currentTimeMillis());
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        //来一个定时7秒才能跑完的任务看看
        ctx.channel().eventLoop().schedule(()->{
            System.out.println("7秒后执行~~~"+System.currentTimeMillis());
        },7, TimeUnit.SECONDS);


    }

    /**
     * 读取完数据后
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("服务器收到了你的消息，谢谢",StandardCharsets.UTF_8));
    }

    /**
     * 如果有异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
