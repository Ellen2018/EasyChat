package com.ellen.library.easy;

import android.os.Handler;

import com.ellen.library.messagehandler.HandlerMessage;
import com.ellen.library.messagehandler.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

import java.util.concurrent.ExecutorService;

/**
 * 传递者(中游)
 */
public abstract class Messenger<T,E> implements HandlerMessage<T,E>, ThreadRunMode<Messenger> {

    private Messenger messenger;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Receiver receiver;
    private Sender sender;
    private Handler handler;
    private ExecutorService executorService;

    void setExecutorService(ExecutorService executorService){
        this.executorService = executorService;
    }

    void setHandler(Handler handler){
        this.handler = handler;
    }

    void setSender(Sender sender){
        this.sender = sender;
    }

    public Messenger setMessenger(Messenger messenger) {
        this.messenger = messenger;
        return this;
    }

    public Receiver setReceiver(Receiver receiver){
        this.receiver = receiver;
        this.receiver.setSender(sender);
        this.receiver.setHandler(handler);
        return receiver;
    }

    @Override
    public void receiverPreMessage(final T sendMessage) {
        if(runMode == RunMode.REUSABLE_THREAD){
            //工作于IO线程
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handleMessage(sendMessage);
                }
            });
        }else if(runMode == RunMode.CURRENT_THREAD){
            //工作于当前线程
            handleMessage(sendMessage);
        }else if(runMode == RunMode.NEW_THREAD){
            //工作于新的线程
            new Thread(){
                @Override
                public void run() {
                    handleMessage(sendMessage);
                }
            }.start();
        }else if(runMode == RunMode.MAIN_THREAD){
            //工作于主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                   handleMessage(sendMessage);
                }
            });
        }else {
            handleMessage(sendMessage);
        }
    }

    @Override
    public void sendMessageToNext(E message) {
        if(messenger != null){
            messenger.receiverPreMessage(message);
            return;
        }
        if(receiver != null){
            receiver.receiverMessage(message);
        }
    }

    @Override
    public Messenger runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }

    public void start(){
        sender.strat();
    }
}
