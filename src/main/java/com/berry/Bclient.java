package com.berry;

import java.io.IOException;

public class Bclient {
    public static void main(String[] args) throws IOException {
        NIOClient nioClient = new NIOClient();
        nioClient.start("Bclient");
    }
}
