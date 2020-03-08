package com.berry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

//客户端线程类 专门接收服务器响应信息
public class NioClientHandle implements Runnable{
    private Selector selector;


    public NioClientHandle(Selector selector) {
        this.selector = selector;
    }
    @Override
    public void run() {
        System.out.println("======run=====");
        //6.循环等待新接入的连接
        try {
        while(true){
            //获取可用channel数量
            int readyChannel = 0;
                readyChannel = selector.select();

            if(readyChannel == 0 )
                continue;;
            //获取可用channel
            Set<SelectionKey> selectionKeys =selector.selectedKeys();
            Iterator iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                //取出可用channel实例
                SelectionKey selectionKey= (SelectionKey) iterator.next();
                //移除set中的当前selectionKey
                iterator.remove();
                //7.根据就绪状态，调用对方方法处理业务逻辑
                //可读事件
                if(selectionKey.isReadable()){
                             readHandle(selectionKey,selector);

                }
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readHandle(SelectionKey selectionKey, Selector selector) throws IOException {
        //要从seletionkey 中获取已经准备就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //循环读服务器请求信息
        String response = "";
        while (socketChannel.read(byteBuffer) > 0){
            //切换buffer为读模式
            byteBuffer.flip();
            //读取buffer中的内容
            response += Charset.forName("UTF-8").decode(byteBuffer);

        }
        //将channel再次注册到seletor 上 监听其他可读事件
        socketChannel.register(selector,SelectionKey.OP_READ);
        if(response .length() >0){
            //将服务器信息回显
            System.out.println("client:" +response);

        }

    }
}
