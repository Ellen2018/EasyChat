package com.ellen.library.easyinterface.receiver;

public interface ReceiverController<T> {

    /**
     * 接收上游的消息
     * @param message
     */
    void receiverMessage(T message);

    /**
     * 接收上游的错误
     * @param throwable
     */
    void receiverErrMessage(Throwable throwable);

    /**
     * 接收来自上游的完成消息
     */
    void receiverCompeteMessage();
}
