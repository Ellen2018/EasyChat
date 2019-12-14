package com.ellen.library.parallel;

import android.os.Handler;

import java.util.List;

/**
 * 能完成并行需求
 */
public abstract class ParallelSender {

    /**
     * 拦截者
     */
    private ParallelMessgener parallelMessgener;
    /**
     *
     */
    private ParallelReceiver parallelReceiver;
    private List<ParallelSender> parallelSendersList;
    private ParallelMessageManager parallelMessageManager;
    private Handler handler = new Handler();

    void setParallelSendersList(List<ParallelSender> parallelSendersList) {
        this.parallelSendersList = parallelSendersList;
    }

    public ParallelSender addParallelSender(ParallelSender parallelSender) {
        parallelSendersList.add(parallelSender);
        return this;
    }

    public ParallelMessgener setParallelMessgener(ParallelMessgener parallelMessgener) {
        this.parallelMessgener = parallelMessgener;
        this.parallelMessgener.setParallelMessageManager(parallelMessageManager);
        this.parallelMessgener.setParallelSendersList(parallelSendersList);
        this.parallelMessgener.setHandler(handler);
        return this.parallelMessgener;
    }

    public ParallelReceiver setParallelReceiver(ParallelReceiver parallelReceiver) {
        this.parallelReceiver = parallelReceiver;
        this.parallelReceiver.setParallelMessageManager(parallelMessageManager);
        this.parallelReceiver.setParallelSendersList(parallelSendersList);
        this.parallelReceiver.setHandler(handler);
        return this.parallelReceiver;
    }

    void setParallelMessageManager(ParallelMessageManager parallelMessageManager) {
        this.parallelMessageManager = parallelMessageManager;
    }

    void setNull(){
        parallelMessgener = null;
        parallelReceiver = null;
    }

    void reSet(ParallelMessgener parallelMessgener,ParallelReceiver parallelReceiver){
        this.parallelMessgener = parallelMessgener;
        this.parallelReceiver = parallelReceiver;
    }

    protected abstract void handlerInstruction();

    public void sendMessageToNext(Object o) {
        if (parallelMessgener != null) {
            parallelMessgener.receiverMessage(o);
        }
        if (parallelReceiver != null) {
            parallelReceiver.receiverMessage(o);
        }
    }
}
