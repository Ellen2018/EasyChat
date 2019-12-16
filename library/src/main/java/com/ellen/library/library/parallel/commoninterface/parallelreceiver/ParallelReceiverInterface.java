package com.ellen.library.library.parallel.commoninterface.parallelreceiver;

import com.ellen.library.library.parallel.ParallelSender;
import com.ellen.library.library.parallel.commoninterface.parallelmessgener.ParallelMessgengrHandler;

public abstract class ParallelReceiverInterface implements ParallelReceiverHandler{

    protected abstract void receiverMessage(ParallelSender parallelSender,Object message);
    protected abstract void receiverErrMessage(ParallelSender parallelSender,Throwable throwable);
    protected abstract void receiverComplete(ParallelSender parallelSender, ParallelMessgengrHandler.TaskProgress taskProgress,Object message);

}
