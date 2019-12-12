package com.ellen.library.easy;

import android.os.Handler;

import com.ellen.library.messagehandler.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

/**
 * 接收者(下游)
 */
public abstract class Receiver<T> implements ThreadRunMode<Receiver> {

    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Sender sender;
    private Handler handler;

    public void setSender(Sender sender){
        this.sender = sender;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void sendMessage(final T sendMessage) {
        if(runMode.equals(RunMode.IO)){
          new Thread(){

              @Override
              public void run() {
                 receiverMessage(sendMessage);
              }
          }.start();
        }else if(runMode.equals(RunMode.NEW_THREAD)){

        }else if(runMode.equals(RunMode.CURRENT_THREAD)){
            receiverMessage(sendMessage);
        }else if(runMode.equals(RunMode.MAIN_THREAD)){
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
    public Receiver runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }

    public void start(){
        sender.strat();
    }

    public abstract void receiverMessage(Object message);
}
