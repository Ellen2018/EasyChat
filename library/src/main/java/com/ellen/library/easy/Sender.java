package com.ellen.library.easy;

import android.os.Handler;
import android.util.Log;

import com.ellen.library.messagehandler.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

/**
 * 发送者(上游)
 */
public abstract class Sender<T> implements ThreadRunMode<Sender> {

    private  T t;
    private Messenger messenger;
    private Receiver receiver;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Handler handler = new Handler();

    public Sender(T t){
        this.t = t;
    }

    public abstract void handlerInstruction(T t);

    public void sendToNextMessage(Object sendMessage){
         if(messenger != null){
             Log.e("Ellen2018","发送者发送消息");
            messenger.sendMessage(sendMessage);
         }
         if(receiver != null){
             receiver.sendMessage(sendMessage);
         }
    }

    public Receiver setReceiver(Receiver receiver){
        this.receiver = receiver;
        this.receiver.setSender(this);
        this.receiver.setHandler(handler);
        return receiver;
    }

    public Messenger setMessenger(Messenger messenger) {
        this.messenger = messenger;
        this.messenger.setSender(this);
        this.messenger.setHandler(handler);
        return this.messenger;
    }

    public void strat(){
        Log.e("Ellen2018","线程模式:"+runMode);
        Log.e("Ellen2018","开始异步");
        if(runMode.equals(RunMode.IO)){
            //工作于IO线程
            new Thread(){
                @Override
                public void run() {
                    handlerInstruction(t);
                }
            }.start();
        }else if(runMode.equals(RunMode.CURRENT_THREAD)){
            //工作于当前线程
            Log.e("Ellen2018","CURRENT_THREAD");
            handlerInstruction(t);
        }else if(runMode.equals(RunMode.NEW_THREAD)){
            //工作于新的线程
        }else if(runMode.equals(RunMode.MAIN_THREAD)){
            //工作于主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handlerInstruction(t);
                }
            });
        }else {
            handlerInstruction(t);
        }
    }

    public Sender runOn(RunMode runMode){
        this.runMode = runMode;
        return this;
    }
}
