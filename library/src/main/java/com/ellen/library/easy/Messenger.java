package com.ellen.library.easy;

import android.os.Handler;

import com.ellen.library.easyinterface.messenger.MessengerHandler;
import com.ellen.library.easyinterface.messenger.MessengerSender;
import com.ellen.library.easyinterface.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

import java.util.concurrent.ExecutorService;

/**
 * 传递者(中游)
 */
public abstract class Messenger<T, E> extends MessengerHandler<T,E> implements ThreadRunMode<Messenger> {

    private Messenger messenger;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Receiver receiver;
    private Sender sender;
    private Handler handler;
    private ExecutorService executorService;
    private MessengerSender<E> messengerSender;

    public Messenger() {
        messengerSender = new MessengerSender<E>() {

            @Override
            public void sendMessageToNext(E message) {
                if (messenger != null) {
                    messenger.receiverPreMessage(message);
                }
                if (receiver != null) {
                    receiver.receiverMessage(message);
                }
            }

            @Override
            public void sendErrMessageToNext(Throwable throwable) {
                if (messenger != null) {
                    messenger.receiverPreErrMessage(throwable);
                }
                if (receiver != null) {
                    receiver.receiverErrMessage(throwable);
                }
            }
        };
    }

    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    void setHandler(Handler handler) {
        this.handler = handler;
    }

    void setSender(Sender sender) {
        this.sender = sender;
    }

    public Messenger setMessenger(Messenger messenger) {
        this.messenger = messenger;
        this.messenger.setSender(sender);
        this.messenger.setHandler(handler);
        this.messenger.setExecutorService(executorService);
        return this.messenger;
    }

    public Receiver setReceiver(Receiver receiver) {
        this.receiver = receiver;
        this.receiver.setSender(sender);
        this.receiver.setHandler(handler);
        return receiver;
    }

    @Override
    protected void receiverPreMessage(final T sendMessage) {
        if (runMode == RunMode.REUSABLE_THREAD) {
            //工作于IO线程
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handleMessage(messengerSender, sendMessage);
                }
            });
        } else if (runMode == RunMode.CURRENT_THREAD) {
            //工作于当前线程
            handleMessage(messengerSender, sendMessage);
        } else if (runMode == RunMode.NEW_THREAD) {
            //工作于新的线程
            new Thread() {
                @Override
                public void run() {
                    handleMessage(messengerSender, sendMessage);
                }
            }.start();
        } else if (runMode == RunMode.MAIN_THREAD) {
            //工作于主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handleMessage(messengerSender, sendMessage);
                }
            });
        } else {
            handleMessage(messengerSender, sendMessage);
        }
    }

    @Override
    protected void receiverPreErrMessage(final Throwable throwable) {
        if (runMode == RunMode.REUSABLE_THREAD) {
            //工作于IO线程
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    handleErrMessage(messengerSender, throwable);
                }
            });
        } else if (runMode == RunMode.CURRENT_THREAD) {
            //工作于当前线程
            handleErrMessage(messengerSender, throwable);
        } else if (runMode == RunMode.NEW_THREAD) {
            //工作于新的线程
            new Thread() {
                @Override
                public void run() {
                    handleErrMessage(messengerSender, throwable);
                }
            }.start();
        } else if (runMode == RunMode.MAIN_THREAD) {
            //工作于主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handleErrMessage(messengerSender, throwable);
                }
            });
        } else {
            handleErrMessage(messengerSender, throwable);
        }
    }

    private void sendCompeteMessage(){
        if(messenger != null){
            messenger.receiverCompeteMessage();
        }
        if(receiver != null){
            receiver.receiverCompeteMessage();
        }
    }

    @Override
    protected void handlerCompete(MessengerSender messengerSender) {
        if(messenger != null){
            messenger.receiverCompeteMessage();
        }
        if(receiver != null){
            receiver.receiverCompeteMessage();
        }
    }

    @Override
    protected void receiverCompeteMessage() {
        if (runMode == RunMode.REUSABLE_THREAD) {
            //工作于IO线程
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                  sendCompeteMessage();
                }
            });
        } else if (runMode == RunMode.CURRENT_THREAD) {
            //工作于当前线程
            sendCompeteMessage();
        } else if (runMode == RunMode.NEW_THREAD) {
            //工作于新的线程
            new Thread() {
                @Override
                public void run() {
                    sendCompeteMessage();
                }
            }.start();
        } else if (runMode == RunMode.MAIN_THREAD) {
            //工作于主线程
            handler.post(new Runnable() {
                @Override
                public void run() {
                    sendCompeteMessage();
                }
            });
        } else {
            sendCompeteMessage();
        }
    }

    @Override
    public Messenger runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }

    public void start() {
        sender.strat();
    }
}
