package com.mstyushin.example.netty;

import com.mstyushin.example.protocol.ResponseInfo;
import com.mstyushin.example.protocol.Result;
import com.mstyushin.example.protocol.StatusCode;

public class ErrorHandler {
    public static Result error(int errorCode) {
        Result result = new Result<>(new ResponseInfo());
        result.getResponseInfo().setCode(errorCode).setCodeMessage(StatusCode.codeMap.get(errorCode));
        return result;
    }

    public static Result error(int errorCode,String parameter) {
        Result result = new Result<>(new ResponseInfo());
        result.getResponseInfo()
                .setCode(errorCode)
                .setCodeMessage(String.format(StatusCode.codeMap.get(errorCode), parameter));
        return result;
    }
}
