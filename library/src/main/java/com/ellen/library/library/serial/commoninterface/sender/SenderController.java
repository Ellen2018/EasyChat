package com.ellen.library.library.serial.commoninterface.sender;

public interface SenderController<T> {

    /**
     * 发送消息给下游
     * @param message
     */
    void sendMessageToNext(T message);

    /**
     * 发送错误的消息给下游
     * @param throwable
     */
    void sendErrMessageToNext(Throwable throwable);

    /**
     *
     */
    void complete();
}
