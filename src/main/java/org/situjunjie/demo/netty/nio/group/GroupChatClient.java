package org.situjunjie.demo.netty.nio.group;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author situjunjie@foxmail.com
 * @date 2022年02月16日 22:20
 */
public class GroupChatClient {

    private String host;

    private Integer port;

    private Selector selector;

    private SocketChannel socketChannel;

    private String userName;

    public GroupChatClient(String host,Integer port){
        try {
            this.selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            this.host = host;
            this.port = port;
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            userName = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println("客户端 "+socketChannel.getLocalAddress().toString().substring(1) +" 上线");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String message){
        String msg = userName+" 说："+message;
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readMsg(){
        try {
            int count = selector.select();
            if(count>0){
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int read = socketChannel.read(byteBuffer);
                        String msg = new String(byteBuffer.array(),0,read);
                        System.out.println(msg);
                        iterator.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupChatClient client = new GroupChatClient("127.0.0.1", 6667);
        new Thread(()->{
            while(true){
                client.readMsg();
            }
        }).start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String msg = scanner.nextLine();
            client.sendMsg(msg);
        }
    }
}
