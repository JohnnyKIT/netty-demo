package org.situjunjie.demo.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @author situjunjie@come-future.com
 * @date 2022/2/16 16:28
 *
 * 模拟服务端进行NIO通信
 */
public class NIOServer {

    public static void main(String[] args) throws Exception{
        //开启服务端
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //配置为非阻塞
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("注册后selector的selectionKeys个数："+selector.keys().size());

        while(true){
            if(selector.select(1000)==0){
                System.out.println("服务器等待了1秒还没有收到连接");
                continue;
            }else{
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                while(selectionKeyIterator.hasNext()){
                    SelectionKey selectionKey = selectionKeyIterator.next();
                    if(selectionKey.isAcceptable()){
                        //监听到连接
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector,SelectionKey.OP_READ,ByteBuffer.allocate(1024));
                        System.out.println("客户端连接成功，产生socketChannel");
                        System.out.println("连接后，注册到selector的key数量有"+selector.selectedKeys().size());
                    }
                    if(selectionKey.isReadable()){
                        //监听到写事件
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer byteBuffer = (ByteBuffer) selectionKey.attachment();
                        int read = channel.read(byteBuffer);
                        System.out.println("from 客户端:"+new String(byteBuffer.array(),0,read, StandardCharsets.UTF_8));
                    }
                    //防止重复操作 要移除key
                    selectionKeyIterator.remove();
                }
            }

        }
    }
}
