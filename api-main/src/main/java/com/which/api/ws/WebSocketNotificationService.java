package com.which.api.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 通知服务
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
    private static final ConcurrentHashMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     *
     * @param session 会话
     * @param sid     sid（userIdStr）
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        SESSION_MAP.put(sid, session);
        if (!SESSION_MAP.containsKey(sid)) {
            log.info("客户端：{} 建立连接", sid);
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
        if (SESSION_MAP.containsKey(sid)) {
            SESSION_MAP.remove(sid);
            log.info("客户端：{} 断开连接", sid);
        }
    }

    /**
     * 连接失败调用的方法
     *
     * @param session 会话
     * @param t       t
     */
    @OnError
    public void onError(Session session, Throwable t) {
        log.info("客户端：{} 连接失败", t.getMessage());
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
            log.warn("客户端 {} 不在线，无法发送通知", sid);
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