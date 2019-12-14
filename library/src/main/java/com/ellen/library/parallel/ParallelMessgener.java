package com.ellen.library.parallel;

import android.os.Handler;
import android.util.Log;

import com.ellen.library.easyinterface.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ParallelMessgener implements ThreadRunMode<ParallelMessgener> {

    private ParallelReceiver parallelReceiver;
    private List<ParallelSender> parallelSendersList;
    private ParallelMessageManager parallelMessageManager;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Handler handler;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private int currentWanChengCount = 0;

    public ParallelReceiver setParallelReceiver(ParallelReceiver parallelReceiver) {
        this.parallelReceiver = parallelReceiver;
        this.parallelReceiver.setParallelMessageManager(parallelMessageManager);
        this.parallelReceiver.setParallelSendersList(parallelSendersList);
        this.parallelReceiver.setParallelMessgener(this);
        this.parallelReceiver.setExecutorService(executorService);
        this.parallelReceiver.setHandler(handler);
        return this.parallelReceiver;
    }

    void setHandler(Handler handler){
        this.handler = handler;
    }

    public void setParallelSendersList(List<ParallelSender> parallelSendersList) {
        this.parallelSendersList = parallelSendersList;
    }

    void setParallelMessageManager(ParallelMessageManager parallelMessageManager) {
        this.parallelMessageManager = parallelMessageManager;
    }

    @Override
    public ParallelMessgener runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }

    void receiverMessage(final Object message){
        currentWanChengCount ++;
        if(runMode == RunMode.NEW_THREAD) {
            new Thread(){
                @Override
                public void run() {
                    handlerMessage(message);
                    handlerMessage(currentWanChengCount,parallelSendersList.size(),message);
                }
            }.start();
        }else if(runMode == RunMode.CURRENT_THREAD){
            handlerMessage(message);
            handlerMessage(currentWanChengCount,parallelSendersList.size(),message);
        }else if(runMode == RunMode.REUSABLE_THREAD){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                  handlerMessage(message);
                  handlerMessage(currentWanChengCount,parallelSendersList.size(),message);
                }
            });
        }else if(runMode == RunMode.MAIN_THREAD){
             handler.post(new Runnable() {
                 @Override
                 public void run() {
                    handlerMessage(message);
                    handlerMessage(currentWanChengCount,parallelSendersList.size(),message);
                 }
             });
        }
    }

    public abstract void handlerMessage(Object message);
    public abstract void handlerMessage(int currentWanChen,int allCount,Object message);

    public void sendMessage(Object message){
        if(parallelReceiver != null){
            parallelReceiver.receiverMessage(message);
        }
    }

}
