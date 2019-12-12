package com.ellen.library.easy;

import android.os.Handler;

import com.ellen.library.messagehandler.HandlerMessage;
import com.ellen.library.messagehandler.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

/**
 * 传递者(中游)
 */
public abstract class Messenger<T> implements HandlerMessage<T>, ThreadRunMode<Messenger> {

    private Messenger messenger;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Receiver receiver;
    private Sender sender;
    private Handler handler;

    public void setHandler(Handler handler){
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
        return receiver;
    }

    @Override
    public void sendMessage(final T sendMessage) {
        if(runMode == RunMode.IO){
            //工作于IO线程
            new Thread(){
                @Override
                public void run() {
                    receiverMessage(sendMessage);
                }
            }.start();
        }else if(runMode == RunMode.CURRENT_THREAD){
            //工作于当前线程
            receiverMessage(sendMessage);
        }else if(runMode == RunMode.NEW_THREAD){
            //工作于新的线程
        }else if(runMode == RunMode.MAIN_THREAD){
            //工作于主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                   receiverMessage(sendMessage);
                }
            });
        }else {
            receiverMessage(sendMessage);
        }
    }

    @Override
    public void sendMessageToNext(Object message) {
        if(messenger != null){
            messenger.sendMessage(message);
            return;
        }
        if(receiver != null){
            receiver.sendMessage(message);
        }
    }

    @Override
    public Messenger runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }
}
