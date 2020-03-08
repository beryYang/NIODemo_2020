package com.berry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

//服务器端

public class NIOServer {
    /**
     * 服务器启动
     */
    public void start() throws IOException {
        //1.
        Selector selector =  Selector.open();
        //2.创建channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3.为channel通道绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8000));
        //4.设置channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        //5.将channel注册到selector上 监听连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //6.循环等待新接入的连接
        while(true){
            //获取可用channel数量
           int readyChannel = selector.select();
           if(readyChannel == 0 )
               continue;;
               //获取可用channel
           Set<SelectionKey>  selectionKeys =selector.selectedKeys();
           Iterator iterator = selectionKeys.iterator();
           while (iterator.hasNext()){
               //取出可用channel实例
               SelectionKey selectionKey= (SelectionKey) iterator.next();
               //移除set中的当前selectionKey
               iterator.remove();
               //7.根据就绪状态，调用对方方法处理业务逻辑

               //接入事件
               if(selectionKey.isAcceptable()){
                   acceptHandler(serverSocketChannel,selector);

               }
               //可读事件
               if(selectionKey.isReadable()){
                   readHandle(selectionKey,selector);
               }
           }
        }


    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel,Selector selector) throws IOException {
        //如果是接入事件 创建socketChannel
       SocketChannel socketChannel =  serverSocketChannel.accept();
        //将socketChannel设置为非阻塞工作模式
        socketChannel.configureBlocking(false);
        //将channel注册到selector上 监听可读事件
        socketChannel.register(selector,SelectionKey.OP_READ);
        //回复客户端提示的信息
        socketChannel.write(Charset.forName("UTF-8").encode("您与聊天室其他人都不是朋友关系，请注意隐私安全"));

    }
    public void readHandle(SelectionKey selectionKey,Selector selector) throws IOException {
        //要从seletionkey 中获取已经准备就绪的channel
       SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读取客户端请求信息
        String request = "";
        while (socketChannel.read(byteBuffer) > 0){
            //切换buffer为读模式
            byteBuffer.flip();
            //读取buffer中的内容
            request += Charset.forName("UTF-8").decode(byteBuffer);

        }
        //将channel再次注册到seletor 上 监听其他可读事件
        socketChannel.register(selector,SelectionKey.OP_READ);
        if(request .length() >0){
            //将客户端发送的请求信息，广播给其他客户端
          //  System.out.println("server" +request);
            broadCast(selector,socketChannel,request);

        }

    }


    /**
     * 广播到其他客户端
     */

    public void broadCast(Selector selector,SocketChannel sourceSocketChannel,String request){
        //获取所有已经介入的客户端chanel
       Set<SelectionKey>  selectionKeys = selector.keys();
       selectionKeys.forEach(selectionKey ->{
           Channel targetChannel = selectionKey.channel();
           //剔除发消息的客户端
           if(targetChannel instanceof SocketChannel
                   && targetChannel != sourceSocketChannel){
               try {//将消息发送到targetChannel客户端
                   ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
               } catch (IOException e) {
                   e.printStackTrace();
               }


           }



       });
    }

    /**
     * 主方法
     * @param args
     */
    public static void main(String[] args) throws IOException {
        NIOServer nioServer = new NIOServer();
        nioServer.start();

    }
}
