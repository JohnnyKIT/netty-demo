package org.situjunjie.demo.netty.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.situjunjie.demo.netty.netty.simple.handler.NettyClientHandler;

/**
 * @author situjunjie@foxmail.com
 * @date 2022年02月19日 21:44
 */
public class NettyClinet {

    public static void main(String[] args) {

        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new NettyClientHandler());
            }
        });
        System.out.println("客户端 is ok!");
        try {
            ChannelFuture cf = bootstrap.connect("127.0.0.1", 6668).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }


    }
}
