package com.berry;

import java.io.IOException;

public class Aclient {
    public static void main(String[] args) throws IOException {
        NIOClient nioClient = new NIOClient();
        nioClient.start("Aclient");
    }
}
