package com.mstyushin.example.rest;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiHandler {

    public static byte[] transfer(ChannelHandlerContext ctx, Object msg) {
        return new byte[123];
    }

}
