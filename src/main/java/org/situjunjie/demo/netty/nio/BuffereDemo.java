package org.situjunjie.demo.netty.nio;

import java.nio.IntBuffer;

/**
 * 看下Buffer是啥玩意儿
 */
public class BuffereDemo {

    public static void main(String[] args) {

        IntBuffer buffer = IntBuffer.allocate(5);

        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put(i*2);
        }

        //取出来要flip一下
        buffer.flip();
        while(buffer.hasRemaining()){
            System.out.println(buffer.get());
        }
    }
}
