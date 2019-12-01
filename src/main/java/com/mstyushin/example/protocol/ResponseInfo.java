package com.mstyushin.example.protocol;

public class ResponseInfo {
    private int code = 200;
    private String codeMessage;

    public int getCode() {
        return code;
    }

    public ResponseInfo setCode(int error) {
        this.code = error;
        return this;
    }

    public String getCodeMessage() {
        return codeMessage;
    }

    public void setCodeMessage(String codeMessage) {
        this.codeMessage = codeMessage;
    }
}
