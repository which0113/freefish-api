package com.which.api.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 通知服务
 *
 * @author which
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketNotificationService {

    /**
     * 会话 map
     */
    private static final Map<String, Session> SESSION_MAP = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     *
     * @param session 会话
     * @param sid     sid（userIdStr）
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        if (!SESSION_MAP.containsKey(sid)) {
            log.info("客户端：{} 建立连接", sid);
            SESSION_MAP.put(sid, session);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param sid     sid（userIdStr）
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到来自客户端：{} 的信息：{}", sid, message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid sid（userIdStr）
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("连接断开：{}", sid);
        SESSION_MAP.remove(sid);
    }

    /**
     * 发送通知给指定用户
     *
     * @param sid     sid（userIdStr）
     * @param message 信息
     */
    public void sendNotification(String sid, String message) {
        Session session = SESSION_MAP.get(sid);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("发送通知给客户端失败", e);
            }
        } else {
            log.warn("用户 {} 不在线，无法发送通知", sid);
        }
    }

    /**
     * 发送通知给所有用户
     *
     * @param message 信息
     */
    public void sendNotification(String message) {
        Collection<Session> sessions = SESSION_MAP.values();
        for (Session session : sessions) {
            try {
                // 服务器向客户端发送消息
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}