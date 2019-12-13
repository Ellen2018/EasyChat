package com.ellen.library.easyinterface.messenger;

public abstract class MessengerReceiver<T> {

    /**
     * 开始接收到来自于上游的消息
     * @param sendMessage
     */
    protected abstract void receiverPreMessage(T sendMessage);

    /**
     * 开始接收来自于上游的错误消息
     */
    protected abstract void receiverPreErrMessage(Throwable throwable);

    /**
     * 开始接收来自于上游的完成的消息
     */
    protected abstract void receiverCompeteMessage();

}
