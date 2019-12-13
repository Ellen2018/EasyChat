package com.ellen.library.easyinterface.messenger;

public interface MessengerHandler<T> {

    /**
     *  已经接受来自于上游的消息
     * @param receiverMessage
     */
    void handleMessage(MessengerSender messengerSender,T receiverMessage);

    /**
     * 处理来自于上游的错误消息
     */
    void handleErrMessage(MessengerSender messengerSender,Throwable throwable);

    /**
     * 来自于上游的完成的消息
     */
    void handlerCompete(MessengerSender messengerSender);

}
