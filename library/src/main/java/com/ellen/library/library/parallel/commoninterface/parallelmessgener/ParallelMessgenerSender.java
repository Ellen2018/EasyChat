package com.ellen.library.library.parallel.commoninterface.parallelmessgener;

import com.ellen.library.library.parallel.ParallelSender;

public interface ParallelMessgenerSender {

    /**
     * 发送消息给下一游
     * @param message
     */
    void sendMessageToNext(ParallelSender parallelSender,Object message);

    /**
     * 发送错误消息给下一游
     * @param throwable
     */
    void sendErrMessageToNext(ParallelSender parallelSender, Throwable throwable);

    /**
     * 发送完成的消息给下一游
     */
    void sendCompleteMessage(ParallelSender parallelSender, ParallelMessgengrHandler.TaskProgress taskProgress,Object message);

}
