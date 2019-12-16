package com.ellen.library.library.parallel.commoninterface.parallelreceiver;

import com.ellen.library.library.parallel.ParallelSender;
import com.ellen.library.library.parallel.commoninterface.parallelmessgener.ParallelMessgengrHandler;

public interface ParallelReceiverHandler {

    void handleMessage(ParallelSender parallelSender,Object message);
    void handleErrMessage(ParallelSender parallelSender, Throwable throwable);
    void handleComplete(ParallelSender parallelSender, ParallelMessgengrHandler.TaskProgress taskProgress,Object message);

}
