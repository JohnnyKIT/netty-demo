package org.situjunjie.demo.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {

    public static void main(String[] args) throws IOException {

        //创建线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();
        //启动服务端
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("当前线程："+Thread.currentThread().getName()+" SocketServer启动成功");
        while(true){
            System.out.println("当前线程："+Thread.currentThread().getName()+" 正在等待连接");
            Socket socket = serverSocket.accept();
            threadPool.submit(()->{
                System.out.println("当前线程："+Thread.currentThread().getName()+" 连接到一个客户端");
                try {
                    handle(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    public static void handle(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        System.out.println("当前线程："+Thread.currentThread().getName()+" 准备read数据");
        while(true){
            byte[] bytes = new byte[1024];
            int read = inputStream.read(bytes);
            if(read!=-1){
                System.out.println("当前线程："+Thread.currentThread().getName()+"读取到客户端数据："+new String(bytes,0,read));
            }else{
                break;
            }
        }

    }
}
