package com.yzc.bankingapp.model;

import org.springframework.stereotype.Component;

@Component
public class ResponseModel {

    private boolean successBool;
    private int bodyType;
    private String message;
    private Object responseBody;

    public boolean isSuccessBool() {
        return successBool;
    }

    public void setSuccessBool(boolean successBool) {
        this.successBool = successBool;
    }

    public int getBodyType() {
        return bodyType;
    }

    public void setBodyType(int bodyType) {
        this.bodyType = bodyType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }
}
