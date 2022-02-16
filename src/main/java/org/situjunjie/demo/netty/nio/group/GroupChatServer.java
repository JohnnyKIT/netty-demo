package org.situjunjie.demo.netty.nio.group;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
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

    public static final int LISTEN_TIME_INTERVAL_MILLISECOND = 1000;

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
            if(selector.select(LISTEN_TIME_INTERVAL_MILLISECOND)>0){
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();
                while (keyIterator.hasNext()){
                    SelectionKey key = keyIterator.next();
                    if(key.isAcceptable()){
                        //可以构建连接
                        SocketChannel sc = serverSocketChannel.accept();
                        sc.configureBlocking(false);
                        sc.register(selector,SelectionKey.OP_READ);
                        System.out.println("客户端："+sc.getRemoteAddress()+" 上线了");
                    }
                    if(key.isReadable()){
                        //监听到可读
                        readData(key);
                    }
                    keyIterator.remove();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 读key中channel的数据
     * @param key
     */
    private void readData(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        try {
            int read = socketChannel.read(byteBuffer);
            //服务器收到消息准备转发
            if(read>0){
                System.out.println("服务端收到消息准备转发");
                String msg = new String(byteBuffer.array(),0,read);
                sendMsg2Other(msg,socketChannel);
            }
        } catch (IOException e) {
            try {
                System.out.println(socketChannel.getRemoteAddress()+" 客户端下线了");
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 服务器转发消息
     * @param msg
     * @param socketChannel
     */
    private void sendMsg2Other(String msg, SocketChannel socketChannel) {
        Set<SelectionKey> keys = selector.keys();
        for(SelectionKey key :keys){
            if(key.channel() instanceof SocketChannel && socketChannel!=key.channel()){
                //除了自己和ServerSocketChannel不转发外都转发到其他客户端
                sendMsg(msg, (SocketChannel) key.channel());
            }
        }
    }

    /**
     * 发送消息到指定channel
     * @param msg
     * @param channel
     */
    private void sendMsg(String msg, SocketChannel channel) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        try {
            channel.write(byteBuffer);
            System.out.println("转发消息完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        GroupChatServer server = new GroupChatServer();
        while (true){
            server.listen();
        }
    }
}
