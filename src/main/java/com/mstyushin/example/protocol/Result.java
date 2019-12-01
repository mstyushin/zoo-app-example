package com.mstyushin.example.protocol;

public class Result<T extends ResponseInfo> {
    protected T responseInfo;

    public T getResponseInfo() {
        return responseInfo;
    }

    public Result(T responseInfo) {
        this.responseInfo = responseInfo;
    }
}
