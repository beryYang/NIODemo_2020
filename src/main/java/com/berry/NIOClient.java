package com.berry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

//客户端
public class NIOClient {
    public void start( String clientName) throws IOException {
        //连接服务器端
        SocketChannel socketChannel  = SocketChannel.open(
                new InetSocketAddress("127.0.0.1",8000));

        //接受服务器响应
        //新开一个线程 用来接受服务器响应的数据
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread((new NioClientHandle(selector))).start();

        //向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String request = scanner.nextLine();
            if(request != null && request.length() >0){
                socketChannel.write(Charset.forName("UTF-8").encode(clientName + ":"+request));
            }
        }

    }

//    public static void main(String[] args) throws IOException {
//        NIOClient client = new NIOClient();
//        client.start();
//    }

}
