package com.ellen.library.library.serial;

import android.os.Handler;

import com.ellen.library.library.runmode.RunMode;
import com.ellen.library.library.runmode.ThreadRunMode;
import com.ellen.library.library.serial.commoninterface.receiver.ReceiverController;

import java.util.concurrent.ExecutorService;

/**
 * 接收者(下游)
 */
public abstract class Receiver<T> extends ReceiverController<T> implements ThreadRunMode<Receiver> {

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

    @Override
    protected void receiverMessage(final T sendMessage) {
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
    protected void receiverErrMessage(final Throwable throwable) {
        if (runMode.equals(RunMode.REUSABLE_THREAD)) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handleErrMessage(throwable);
                }
            });
        } else if (runMode.equals(RunMode.NEW_THREAD)) {
            new Thread(){
                @Override
                public void run() {
                    handleErrMessage(throwable);
                }
            }.start();
        } else if (runMode.equals(RunMode.CURRENT_THREAD)) {
            handleErrMessage(throwable);
        } else if (runMode.equals(RunMode.MAIN_THREAD)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handleErrMessage(throwable);
                }
            });
        } else {
            handleErrMessage(throwable);
        }
    }

    @Override
    protected void receiverCompeteMessage() {
        if (runMode.equals(RunMode.REUSABLE_THREAD)) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    complete();
                }
            });
        } else if (runMode.equals(RunMode.NEW_THREAD)) {
            new Thread(){
                @Override
                public void run() {
                    complete();
                }
            }.start();
        } else if (runMode.equals(RunMode.CURRENT_THREAD)) {
            complete();
        } else if (runMode.equals(RunMode.MAIN_THREAD)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    complete();
                }
            });
        } else {
            complete();
        }
    }

    @Override
    public Receiver runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }

    /**
     * 普通开始
     */
    public void start() {
        sender.strat();
    }
}
