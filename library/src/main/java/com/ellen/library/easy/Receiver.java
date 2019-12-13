package com.ellen.library.easy;

import android.os.Handler;

import com.ellen.library.messagehandler.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

import java.util.concurrent.ExecutorService;

/**
 * 接收者(下游)
 */
public abstract class Receiver<T> implements ThreadRunMode<Receiver> {

    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Sender sender;
    private Handler handler;
    private ExecutorService executorService;

    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    void setSender(Sender sender) {
        this.sender = sender;
    }

    void setHandler(Handler handler) {
        this.handler = handler;
    }

    void receiverMessage(final T sendMessage) {
        if (runMode.equals(RunMode.REUSABLE_THREAD)) {
           executorService.execute(new Runnable() {
               @Override
               public void run() {
                   handleMessage(sendMessage);
               }
           });
        } else if (runMode.equals(RunMode.NEW_THREAD)) {
            new Thread(){
                @Override
                public void run() {
                    handleMessage(sendMessage);
                }
            }.start();
        } else if (runMode.equals(RunMode.CURRENT_THREAD)) {
            handleMessage(sendMessage);
        } else if (runMode.equals(RunMode.MAIN_THREAD)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handleMessage(sendMessage);
                }
            });
        } else {
            handleMessage(sendMessage);
        }
    }

    @Override
    public Receiver runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }

    public void start() {
        sender.strat();
    }

    public abstract void handleMessage(T message);
}
