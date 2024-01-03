package com.which.apiinterface.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import com.which.apisdk.exception.ApiException;
import com.which.apisdk.model.response.ResultResponse;

import java.util.Map;

/**
 * @author which
 */
public class ResponseUtils {
    public static Map<String, Object> responseToMap(String response) {
        return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    public static <T> ResultResponse baseResponse(String baseUrl, T params) {
        String response = null;
        try {
            response = RequestUtils.get(baseUrl, params);
            Map<String, Object> fromResponse = responseToMap(response);
            boolean success = (Boolean) fromResponse.get("success");
            ResultResponse baseResponse = new ResultResponse();
            if (!success) {
                baseResponse.setData(fromResponse);
                return baseResponse;
            }
            fromResponse.remove("success");
            baseResponse.setData(fromResponse);
            return baseResponse;
        } catch (ApiException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "构建url异常");
        }
    }
}
