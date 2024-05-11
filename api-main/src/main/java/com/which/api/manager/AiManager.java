package com.which.api.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.which.apicommon.constant.CommonConstant.GPT_AI_PROMPT;

/**
 * @author which
 */
@Service
public class AiManager {

    private static final Long MODE_ID = 1733756654497263617L;
    @Resource
    private YuCongMingClient congMingClient;
    @Value("${open-ai-client.api-key}")
    private String apiKey;
    @Value("${open-ai-client.api-host}")
    private String apiHost;

    public String doChat(String message) {
        DevChatRequest devChatRequest = new DevChatRequest();
        // 鱼聪明平台模型ID
        devChatRequest.setModelId(MODE_ID);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = congMingClient.doChat(devChatRequest);
        if (response == null || response.getCode() != 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return response.getData().getContent();
    }

    public String doChatByGpt(String content) {
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.set("role", "user");
        message.set("content", GPT_AI_PROMPT + content);
        messages.set(message);

        JSONObject json = new JSONObject();
        json.set("model", "gpt-3.5-turbo");
        json.set("messages", messages);

        String responseBody = HttpRequest.post(apiHost + "/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .body(json.toString())
                .execute()
                .body();

        JSONObject responseJson = JSONUtil.parseObj(responseBody);
        JSONArray choices = responseJson.getJSONArray("choices");

        if (choices.size() == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }

        JSONObject choice = choices.getJSONObject(0);
        return choice.getJSONObject("message").getStr("content");
    }

}