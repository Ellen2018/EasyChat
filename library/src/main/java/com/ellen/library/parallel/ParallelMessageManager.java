package com.ellen.library.parallel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelMessageManager {

    /**
     * 并行的发送者
     */
    private List<ParallelSender> parallelSendersList;
    private ParallelMessgener parallelMessgener;
    private ParallelReceiver parallelReceiver;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public ParallelSender addParallelSender(ParallelSender parallelSender) {
        if (parallelSendersList == null) {
            parallelSendersList = new ArrayList<>();
        }
        parallelSendersList.add(parallelSender);
        parallelSender.setParallelSendersList(parallelSendersList);
        parallelSender.setParallelMessageManager(this);
        return parallelSender;
    }

    void setParallelMessgener(ParallelMessgener parallelMessgener) {
        this.parallelMessgener = parallelMessgener;
    }

    void setParallelReceiver(ParallelReceiver parallelReceiver) {
        this.parallelReceiver = parallelReceiver;
    }

    public void start() {
        for (final ParallelSender parallelSender : parallelSendersList) {
            //将发送者的下一级置空，防止Receiver收到重复的消息，有时间把这里去掉
            parallelSender.setNull();
            parallelSender.reSet(parallelMessgener,parallelReceiver);
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    parallelSender.handlerInstruction();
                }
            });
        }
    }

}
