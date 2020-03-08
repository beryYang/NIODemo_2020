package com.berry;

import java.io.IOException;

public class Cclient {
    public static void main(String[] args) throws IOException {
        NIOClient nioClient = new NIOClient();
        nioClient.start("Cclient");
    }
}
