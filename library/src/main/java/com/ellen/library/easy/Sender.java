package com.ellen.library.easy;

import android.os.Handler;
import android.util.Log;

import com.ellen.library.messagehandler.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 发送者(上游)
 * Sender的泛型代表向下发送怎样类型的消息
 */
public abstract class Sender<T> implements ThreadRunMode<Sender> {

    private Messenger messenger;
    private Receiver receiver;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Handler handler = new Handler();
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public abstract void handlerInstruction();

    public void sendToNextMessage(T sendMessage){
         if(messenger != null){
            messenger.receiverPreMessage(sendMessage);
         }
         if(receiver != null){
             receiver.receiverMessage(sendMessage);
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
        this.messenger.setExecutorService(executorService);
        return this.messenger;
    }

    public void strat(){
        Log.e("Ellen2018","线程模式:"+runMode);
        Log.e("Ellen2018","开始异步");
        if(runMode.equals(RunMode.REUSABLE_THREAD)){
            //工作于IO线程
          executorService.execute(new Runnable() {
              @Override
              public void run() {
                  handlerInstruction();
              }
          });
        }else if(runMode.equals(RunMode.CURRENT_THREAD)){
            //工作于当前线程
            Log.e("Ellen2018","CURRENT_THREAD");
            handlerInstruction();
        }else if(runMode.equals(RunMode.NEW_THREAD)){
            //工作于新的线程
            new Thread(){
                @Override
                public void run() {
                    handlerInstruction();
                }
            }.start();
        }else if(runMode.equals(RunMode.MAIN_THREAD)){
            //工作于主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handlerInstruction();
                }
            });
        }else {
            handlerInstruction();
        }
    }

    public Sender runOn(RunMode runMode){
        this.runMode = runMode;
        return this;
    }
}
