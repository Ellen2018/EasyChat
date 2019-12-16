package com.ellen.library.library.serial.commoninterface.receiver;

public abstract class ReceiverHandler<T> {
    /**
     * 处理来自于上游发送过来的消息
     * @param message
     */
    protected abstract void handleMessage(T message);

    /**
     * 处理来自于上游发送过来的错误
     * @param throwable
     */
    protected abstract void handleErrMessage(Throwable throwable);

    /**
     * 消息接收完成
     */
    protected abstract void complete();
}
