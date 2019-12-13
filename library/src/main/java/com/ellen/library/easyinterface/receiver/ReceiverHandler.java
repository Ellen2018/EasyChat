package com.ellen.library.easyinterface.receiver;

public interface ReceiverHandler<T> {
    /**
     * 处理来自于上游发送过来的消息
     * @param message
     */
    void handleMessage(T message);

    /**
     * 处理来自于上游发送过来的错误
     * @param throwable
     */
    void handleErrMessage(Throwable throwable);

    /**
     * 消息接收完成
     */
    void complete();
}
