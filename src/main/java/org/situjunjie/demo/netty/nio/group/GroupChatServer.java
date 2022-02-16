package org.situjunjie.demo.netty.nio.group;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author situjunjie@come-future.com
 * @date 2022/2/16 18:33
 */
public class GroupChatServer {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public static final int PORT = 6667;

    public static final int LISTEN_TIME_INTERVAL_MILISECOND = 1000;

    public GroupChatServer(){
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听方法
     */
    public void listen(){
        try {
            if(selector.select(LISTEN_TIME_INTERVAL_MILISECOND)>0){
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();
                while (keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();
                    if(key.isAcceptable()){
                        //可以构建连接
                        SocketChannel sc = (SocketChannel) key.channel();
                        sc.register(selector,SelectionKey.OP_READ);
                        System.out.println("客户端："+sc.getRemoteAddress()+" 上线了");
                    }
                    if(key.isReadable()){
                        //TODO 监听到可写
                    }
                    keyIterator.remove();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
