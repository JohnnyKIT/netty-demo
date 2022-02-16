package org.situjunjie.demo.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author situjunjie@come-future.com
 * @date 2022/2/16 16:48
 *
 * 模拟客户端进行NIO通信
 */
public class NIOClient {

    public static void main(String[] args) throws Exception{

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
        if(!socketChannel.connect(address)){
            while(!socketChannel.finishConnect()){
                System.out.println("连接不阻塞，客户端还可以还其工作");
            }
        }

        //如果连接成功就开始发送数据
        String str = "Hello NIO!!";
        socketChannel.write(ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)));

        //阻塞
        System.out.println("程序运行结束");
        System.in.read();
    }
}
