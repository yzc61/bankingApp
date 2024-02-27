package com.yzc.bankingapp;

import com.yzc.bankingapp.model.ResponseModel;
import org.springframework.stereotype.Component;

@Component
public class ResponseModelUtil {

    private static final int emptyBody = 0;
    private static final int stringBody = 1;
    private static final int jsonBody = 2;

    public ResponseModel succesResponseWithModel (String message, Object responseBody){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setBodyType(jsonBody);
        responseModel.setSuccessBool(true);
        responseModel.setMessage(message);
        responseModel.setResponseBody(responseBody);
        return responseModel;
    }

    public ResponseModel succesResponseWithString (String message, Object responseBody){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setBodyType(stringBody);
        responseModel.setSuccessBool(true);
        responseModel.setMessage(message);
        responseModel.setResponseBody(responseBody);
        return responseModel;
    }

    public ResponseModel succesResponseWithoutModel (String message){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setBodyType(emptyBody);
        responseModel.setSuccessBool(true);
        responseModel.setMessage(message);
        responseModel.setResponseBody(null);
        return responseModel;
    }

    public ResponseModel errorUserResponse (String message){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setBodyType(emptyBody);
        responseModel.setSuccessBool(false);
        responseModel.setMessage(message);
        responseModel.setResponseBody(null);
        return responseModel;
    }

    public ResponseModel errorSystemResponse (String message){
        ResponseModel responseModel = new ResponseModel();
        responseModel.setBodyType(emptyBody);
        responseModel.setSuccessBool(false);
        responseModel.setMessage(message);
        responseModel.setResponseBody(null);
        return responseModel;
    }


}
