package com.ellen.library.library.parallel.commoninterface.parallelmessgener;

import com.ellen.library.library.parallel.ParallelSender;

public abstract class ParallelMessgenerReceiver extends ParallelMessgengrHandler{

    protected abstract void receiverMessage(ParallelSender parallelSender,Object message);
    protected abstract void receiverErrMessage(ParallelSender parallelSender,Throwable throwable);
    protected abstract void receiverComplete(ParallelSender parallelSender,Object message);

}
