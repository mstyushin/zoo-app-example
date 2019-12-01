package com.mstyushin.example.resource;

import com.mstyushin.example.netty.ErrorHandler;
import com.mstyushin.example.protocol.ProtocolMessage;
import com.mstyushin.example.protocol.ResponseInfo;
import com.mstyushin.example.protocol.Result;
import com.mstyushin.example.protocol.StatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseResource {

    protected ProtocolMessage protocolMessage;

    public BaseResource(ProtocolMessage protocolMessage) {
        this.protocolMessage = protocolMessage;
    }

    public Object parameterIntCheck(ProtocolMessage apiProtocol, String parameter) {
        if (apiProtocol.getParameters().containsKey(parameter)) {
            try {
                return Integer.parseInt(apiProtocol.getParameters().get(parameter).get(0));
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
                return error(StatusCode.PARAM_FORMAT_ERROR, parameter);
            }
        } else {
            return error(StatusCode.PARAM_CAN_NOT_BE_NULL, parameter);
        }
    }

    protected Result error(int code) {
        return ErrorHandler.error(code);
    }

    protected Result error(int code, String parameter) {
        return ErrorHandler.error(code, parameter);
    }

    protected Result success() {
        return new Result<>(new ResponseInfo());
    }

    protected Result success(int code) {
        Result result = new Result<>(new ResponseInfo());
        result.getResponseInfo().setCode(code).setCodeMessage(StatusCode.codeMap.get(code));
        return result;
    }

}
