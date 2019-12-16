package com.ellen.library.library.serial.commoninterface.messenger;

public abstract class MessengerHandler<T,E> extends MessengerReceiver<T>{

    /**
     *  已经接受来自于上游的消息
     * @param receiverMessage
     */
    protected abstract void handleMessage(MessengerSender<E> messengerSender,T receiverMessage);

    /**
     * 处理来自于上游的错误消息
     */
    protected abstract void handleErrMessage(MessengerSender<E> messengerSender,Throwable throwable);

    /**
     * 来自于上游的完成的消息
     */
    protected abstract void handlerCompete(MessengerSender messengerSender);

}
