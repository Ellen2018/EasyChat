package com.ellen.library.messagehandler;

public interface HandlerMessage<T,E> {

    /**
     * 开始接收到来自于上游的消息
     * @param sendMessage
     */
    void receiverPreMessage(T sendMessage);

    /**
     *  已经接受来自于上游的消息
     * @param receiverMessage
     */
    void handleMessage(T receiverMessage);

    /**
     * 将消息发送给下游
     * @param message
     */
    void sendMessageToNext(E message);

}
