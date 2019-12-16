package com.ellen.library.library.serial.commoninterface.receiver;

public abstract class ReceiverController<T> extends ReceiverHandler<T>{

    /**
     * 接收上游的消息
     * @param message
     */
    protected abstract void receiverMessage(T message);

    /**
     * 接收上游的错误
     * @param throwable
     */
    protected abstract void receiverErrMessage(Throwable throwable);

    /**
     * 接收来自上游的完成消息
     */
    protected abstract void receiverCompeteMessage();
}
