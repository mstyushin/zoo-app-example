package com.mstyushin.example.rest;

import com.mstyushin.example.netty.ErrorHandler;
import com.mstyushin.example.protocol.StatusCode;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiConnector {

    public static byte[] transfer(ChannelHandlerContext ctx, Object msg) {
        ApiProtocol apiProtocol = new ApiProtocol(ctx, msg);

        if (apiProtocol.getEndpoint() == null) {
            return encode(ErrorHandler.error(StatusCode.API_CAN_NOT_BE_NULL));
        }

        if (apiProtocol.getApi() == null) {
            return encode(ErrorHandler.error(StatusCode.API_NOT_FOUND));
        }

        Object result = invoke(apiProtocol.getApi(), apiProtocol);
        if (result == null) {
            return encode(ErrorHandler.error(StatusCode.UNKNOWN_ERROR));
        }

        return encode(result);
    }

}
