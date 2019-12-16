package com.ellen.library.library.parallel;

import android.os.Handler;

import com.ellen.library.library.parallel.commoninterface.parallelmessgener.ParallelMessgenerReceiver;
import com.ellen.library.library.parallel.commoninterface.parallelmessgener.ParallelMessgenerSender;
import com.ellen.library.library.runmode.RunMode;
import com.ellen.library.library.runmode.ThreadRunMode;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ParallelMessgener extends ParallelMessgenerReceiver implements ThreadRunMode<ParallelMessgener> {

    private ParallelReceiver parallelReceiver;
    private List<ParallelSender> parallelSendersList;
    private ParallelMessageManager parallelMessageManager;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Handler handler;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private Hashtable<ParallelSender, Integer> parallelSenderIntegerHashtable;
    private ParallelMessgenerSender parallelMessgenerSender;

    public ParallelReceiver setParallelReceiver(ParallelReceiver parallelReceiver) {
        this.parallelReceiver = parallelReceiver;
        this.parallelReceiver.setParallelMessageManager(parallelMessageManager);
        this.parallelReceiver.setParallelSendersList(parallelSendersList);
        this.parallelReceiver.setParallelMessgener(this);
        this.parallelReceiver.setExecutorService(executorService);
        this.parallelReceiver.setHandler(handler);
        return this.parallelReceiver;
    }

    public ParallelMessgener(){
       parallelMessgenerSender = new ParallelMessgenerSender() {
           @Override
           public void sendMessageToNext(ParallelSender parallelSender, Object message) {
                   if(parallelReceiver != null){
                       parallelReceiver.receiverMessage(parallelSender,message);
                   }
           }

           @Override
           public void sendErrMessageToNext(ParallelSender parallelSender, Throwable throwable) {
               if(parallelReceiver != null){
                   parallelReceiver.receiverErrMessage(parallelSender,throwable);
               }
           }

           @Override
           public void sendCompleteMessage(ParallelSender parallelSender,TaskProgress taskProgress,Object message) {
               if(parallelReceiver != null){
                   parallelReceiver.receiverComplete(parallelSender,taskProgress,message);
               }
           }
       };
    }

    void setHandler(Handler handler) {
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


    protected void receiverMessage(final ParallelSender parallelSender, final Object message) {
        if (runMode == RunMode.NEW_THREAD) {
            new Thread() {
                @Override
                public void run() {
                    handlerMessage(parallelMessgenerSender,parallelSender, message);
                }
            }.start();
        } else if (runMode == RunMode.CURRENT_THREAD) {
            handlerMessage(parallelMessgenerSender,parallelSender, message);
        } else if (runMode == RunMode.REUSABLE_THREAD) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    handlerMessage(parallelMessgenerSender,parallelSender, message);
                }
            });
        } else if (runMode == RunMode.MAIN_THREAD) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handlerMessage(parallelMessgenerSender,parallelSender, message);
                }
            });
        }
    }

    protected void receiverErrMessage(final ParallelSender parallelSender, final Throwable throwable) {
        if (runMode == RunMode.NEW_THREAD) {
            new Thread() {
                @Override
                public void run() {
                    handlerErrMessage(parallelMessgenerSender,parallelSender, throwable);
                }
            }.start();
        } else if (runMode == RunMode.CURRENT_THREAD) {
            handlerErrMessage(parallelMessgenerSender,parallelSender, throwable);
        } else if (runMode == RunMode.REUSABLE_THREAD) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    handlerErrMessage(parallelMessgenerSender,parallelSender, throwable);
                }
            });
        } else if (runMode == RunMode.MAIN_THREAD) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handlerErrMessage(parallelMessgenerSender,parallelSender, throwable);
                }
            });
        } else {
            handlerErrMessage(parallelMessgenerSender,parallelSender, throwable);
        }
    }

    @Override
    protected synchronized void receiverComplete(final ParallelSender parallelSender, final Object message) {
        if (parallelSenderIntegerHashtable == null) {
            parallelSenderIntegerHashtable = new Hashtable<>();
        }
        parallelSenderIntegerHashtable.put(parallelSender, parallelSenderIntegerHashtable.size() + 1);
        if (runMode == RunMode.NEW_THREAD) {
            new Thread() {
                @Override
                public void run() {
                    TaskProgress taskProgress = new
                            TaskProgress(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size());
                    handlerComplete(parallelMessgenerSender,parallelSender,taskProgress,message);
                }
            }.start();
        } else if (runMode == RunMode.CURRENT_THREAD) {
            TaskProgress taskProgress = new
                    TaskProgress(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size());
            handlerComplete(parallelMessgenerSender,parallelSender,taskProgress,message);
        } else if (runMode == RunMode.REUSABLE_THREAD) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    TaskProgress taskProgress = new
                            TaskProgress(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size());
                    handlerComplete(parallelMessgenerSender,parallelSender,taskProgress,message);
                }
            });
        } else if (runMode == RunMode.MAIN_THREAD) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    TaskProgress taskProgress = new
                            TaskProgress(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size());
                    handlerComplete(parallelMessgenerSender,parallelSender,taskProgress,message);
                }
            });
        } else {
            TaskProgress taskProgress = new
                    TaskProgress(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size());
            handlerComplete(parallelMessgenerSender,parallelSender,taskProgress,message);
        }
    }
}
