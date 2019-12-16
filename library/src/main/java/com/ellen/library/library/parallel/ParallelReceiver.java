package com.ellen.library.library.parallel;

import android.os.Handler;


import com.ellen.library.library.parallel.commoninterface.parallelmessgener.ParallelMessgengrHandler;
import com.ellen.library.library.parallel.commoninterface.parallelreceiver.ParallelReceiverInterface;
import com.ellen.library.library.runmode.RunMode;
import com.ellen.library.library.runmode.ThreadRunMode;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class ParallelReceiver extends ParallelReceiverInterface implements ThreadRunMode<ParallelReceiver>{

    private List<ParallelSender> parallelSendersList;
    private ParallelMessageManager parallelMessageManager;
    private Handler handler;
    private ParallelMessgener parallelMessgener;
    private ExecutorService executorService;
    private RunMode runMode = RunMode.CURRENT_THREAD;
    private Hashtable<ParallelSender, Integer> parallelSenderIntegerHashtable;

    void setParallelMessageManager(ParallelMessageManager parallelMessageManager) {
        this.parallelMessageManager = parallelMessageManager;
    }

    public void setParallelSendersList(List<ParallelSender> parallelSendersList) {
        this.parallelSendersList = parallelSendersList;
    }

    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    void setParallelMessgener(ParallelMessgener parallelMessgener){
        this.parallelMessgener = parallelMessgener;
    }

    void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected void receiverMessage(final ParallelSender parallelSender, final Object message) {
        if (runMode.equals(RunMode.NEW_THREAD)) {
            new Thread() {
                @Override
                public void run() {
                    handleMessage(parallelSender,message);
                }
            }.start();
        } else if (runMode.equals(RunMode.CURRENT_THREAD)) {
            handleMessage(parallelSender,message);
        } else if (runMode.equals(RunMode.REUSABLE_THREAD)) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    handleMessage(parallelSender,message);
                }
            });
        } else if (runMode.equals(RunMode.MAIN_THREAD)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handleMessage(parallelSender,message);
                }
            });
        } else {
            handleMessage(parallelSender,message);
        }
    }

    protected void receiverErrMessage(final ParallelSender parallelSender, final Throwable throwable){
        if (runMode.equals(RunMode.NEW_THREAD)) {
            new Thread() {
                @Override
                public void run() {
                    handleErrMessage(parallelSender,throwable);
                }
            }.start();
        } else if (runMode.equals(RunMode.CURRENT_THREAD)) {
            handleErrMessage(parallelSender,throwable);
        } else if (runMode.equals(RunMode.REUSABLE_THREAD)) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    handleErrMessage(parallelSender,throwable);
                }
            });
        } else if (runMode.equals(RunMode.MAIN_THREAD)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handleErrMessage(parallelSender,throwable);
                }
            });
        } else {
            handleErrMessage(parallelSender,throwable);
        }
    }

    @Override
    protected void receiverComplete(final ParallelSender parallelSender, final ParallelMessgengrHandler.TaskProgress taskProgress, final Object message) {
        if (runMode.equals(RunMode.NEW_THREAD)) {
            new Thread() {
                @Override
                public void run() {
                    handleComplete(parallelSender,taskProgress,message);
                }
            }.start();
        } else if (runMode.equals(RunMode.CURRENT_THREAD)) {
            handleComplete(parallelSender,taskProgress,message);
        } else if (runMode.equals(RunMode.REUSABLE_THREAD)) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    handleComplete(parallelSender,taskProgress,message);
                }
            });
        } else if (runMode.equals(RunMode.MAIN_THREAD)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    handleComplete(parallelSender,taskProgress,message);
                }
            });
        } else {
            handleComplete(parallelSender,taskProgress,message);
        }
    }

    void receiverCompleteFromSender(final ParallelSender parallelSender,Object message){
        if(parallelSenderIntegerHashtable == null) {
            parallelSenderIntegerHashtable = new Hashtable<>();
        }
        parallelSenderIntegerHashtable.put(parallelSender,parallelSenderIntegerHashtable.size() + 1);
        ParallelMessgengrHandler.TaskProgress taskProgress = new ParallelMessgengrHandler.
                TaskProgress(parallelSenderIntegerHashtable.get(parallelSender),parallelSendersList.size());
        receiverComplete(parallelSender,taskProgress,message);
    }

    @Override
    public ParallelReceiver runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }

    public void start() {
        if(parallelMessgener != null){
          parallelMessageManager.setParallelMessgener(parallelMessgener);
        }else {
            parallelMessageManager.setParallelReceiver(this);
        }
        parallelMessageManager.start();
    }

}
