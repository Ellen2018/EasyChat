package com.ellen.library.library.serial;

import android.os.Handler;

import com.ellen.library.library.runmode.RunMode;
import com.ellen.library.library.runmode.ThreadRunMode;
import com.ellen.library.library.serial.commoninterface.sender.SenderController;
import com.ellen.library.library.serial.commoninterface.sender.SenderHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 发送者(上游)
 * Sender的泛型代表向下发送怎样类型的消息
 */
public abstract class Sender<T> extends SenderHandler<T> implements ThreadRunMode<Sender> {

    private Messenger messenger;
    private Receiver receiver;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Handler handler = new Handler();
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private SenderController<T> senderController;

    public Sender(){
        senderController = new SenderController<T>() {

            @Override
            public void sendMessageToNext(T message) {
               if(messenger != null){
                   messenger.receiverPreMessage(message);
               }
               if(receiver != null){
                   receiver.receiverMessage(message);
               }
            }

            @Override
            public void sendErrMessageToNext(Throwable throwable) {
                if(messenger != null){
                    messenger.receiverPreErrMessage(throwable);
                }
                if(receiver != null){
                    receiver.receiverErrMessage(throwable);
                }
            }

            @Override
            public void complete() {
                if(messenger != null){
                    messenger.receiverCompeteMessage();
                }
                if(receiver != null){
                    receiver.receiverCompeteMessage();
                }
            }
        };
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
        if(runMode.equals(RunMode.REUSABLE_THREAD)){
            //工作于IO线程
          executorService.execute(new Runnable() {
              @Override
              public void run() {
                  handlerInstruction(senderController);
              }
          });
        }else if(runMode.equals(RunMode.CURRENT_THREAD)){
            //工作于当前线程
            handlerInstruction(senderController);
        }else if(runMode.equals(RunMode.NEW_THREAD)){
            //工作于新的线程
            new Thread(){
                @Override
                public void run() {
                    handlerInstruction(senderController);
                }
            }.start();
        }else if(runMode.equals(RunMode.MAIN_THREAD)){
            //工作于主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handlerInstruction(senderController);
                }
            });
        }else {
            handlerInstruction(senderController);
        }
    }

    public Sender runOn(RunMode runMode){
        this.runMode = runMode;
        return this;
    }
}
