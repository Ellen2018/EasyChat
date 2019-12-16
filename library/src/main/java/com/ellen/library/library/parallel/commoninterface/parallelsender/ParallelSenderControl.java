package com.ellen.library.library.parallel.commoninterface.parallelsender;

public interface ParallelSenderControl {

    /**
     * 发送消息给下一游
     * @param o
     */
    void sendMessageToNext(Object o);

    /**
     * 发送错误消息给下一游
     * @param throwable
     */
    void sendErrMessageToNext(Throwable throwable);

    /**
     * 发送完成的消息给下一游
     */
    void sendCompleteMessage(Object message);

}
