package com.ellen.library.easyinterface.messenger;

public interface MessengerReceiver<T> {

    /**
     * 开始接收到来自于上游的消息
     * @param sendMessage
     */
    void receiverPreMessage(T sendMessage);

    /**
     * 开始接收来自于上游的错误消息
     */
    void receiverPreErrMessage(Throwable throwable);

    /**
     * 开始接收来自于上游的完成的消息
     */
    void receiverCompeteMessage();

}
