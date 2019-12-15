package com.ellen.library.parallel;

import android.os.Handler;
import android.util.Log;

import com.ellen.library.easyinterface.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ParallelMessgener implements ThreadRunMode<ParallelMessgener> {

    private ParallelReceiver parallelReceiver;
    private List<ParallelSender> parallelSendersList;
    private ParallelMessageManager parallelMessageManager;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Handler handler;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private Hashtable<ParallelSender,Integer> parallelSenderIntegerHashtable;

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

    synchronized void receiverMessage(final ParallelSender parallelSender, final Object message){
        if(parallelSenderIntegerHashtable == null){
            parallelSenderIntegerHashtable = new Hashtable<>();
        }
        parallelSenderIntegerHashtable.put(parallelSender,parallelSenderIntegerHashtable.size()+1);
        if(runMode == RunMode.NEW_THREAD) {
            new Thread(){
                @Override
                public void run() {
                    handlerMessage(parallelSender.getTag(),message);
                    handlerMessage(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size(),parallelSender.getTag(),message);
                }
            }.start();
        }else if(runMode == RunMode.CURRENT_THREAD){
            handlerMessage(parallelSender.getTag(),message);
            handlerMessage(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size(),parallelSender.getTag(),message);
        }else if(runMode == RunMode.REUSABLE_THREAD){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                  handlerMessage(parallelSender.getTag(),message);
                  handlerMessage(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size(),parallelSender.getTag(),message);
                }
            });
        }else if(runMode == RunMode.MAIN_THREAD){
             handler.post(new Runnable() {
                 @Override
                 public void run() {
                    handlerMessage(parallelSender.getTag(),message);
                    handlerMessage(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size(),parallelSender.getTag(),message);
                 }
             });
        }
    }

    void receiverErrMessage(final ParallelSender parallelSender, final Throwable throwable){
        if(runMode == RunMode.NEW_THREAD) {
           new Thread(){
               @Override
               public void run() {
                  handlerErrMessage(parallelSender,throwable);
               }
           }.start();
        }else if(runMode == RunMode.CURRENT_THREAD){
            handlerErrMessage(parallelSender,throwable);
        }else if(runMode == RunMode.REUSABLE_THREAD){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    handlerErrMessage(parallelSender,throwable);
                }
            });
        }else if(runMode == RunMode.MAIN_THREAD){
           handler.post(new Runnable() {
               @Override
               public void run() {
                   handlerErrMessage(parallelSender,throwable);
               }
           });
        }else {
            handlerErrMessage(parallelSender,throwable);
        }
    }

    public abstract void handlerMessage(String tag,Object message);
    public abstract void handlerMessage(int currentWanChen,int allCount,String tag,Object message);
    public abstract void handlerErrMessage(ParallelSender parallelSender,Throwable throwable);

    public void sendMessage(Object message){
        if(parallelReceiver != null){
            parallelReceiver.receiverMessage(message);
        }
    }

    public void sendErrMessage(ParallelSender parallelSender,Throwable throwable){
        if(parallelReceiver != null){
            parallelReceiver.receiverErrMessage(parallelSender,throwable);
        }
    }

}
