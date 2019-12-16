package com.ellen.library.library.serial.commoninterface.messenger;

public interface MessengerSender<E> {

    /**
     * 将消息发送给下游
     * @param message
     */
    void sendMessageToNext(E message);

    /**
     * 发送错误消息给下游
     */
    void sendErrMessageToNext(Throwable throwable);

}
