package com.ellen.library.parallel;

import android.os.Handler;
import android.util.Log;

import com.ellen.library.easyinterface.ThreadRunMode;
import com.ellen.library.runmode.RunMode;

import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class ParallelReceiver implements ThreadRunMode<ParallelReceiver> {

    private List<ParallelSender> parallelSendersList;
    private ParallelMessageManager parallelMessageManager;
    private Handler handler;
    private ParallelMessgener parallelMessgener;
    private ExecutorService executorService;
    private RunMode runMode = RunMode.CURRENT_THREAD;

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

    void receiverMessage(final Object message) {
        if (runMode.equals(RunMode.NEW_THREAD)) {
            new Thread() {
                @Override
                public void run() {
                    handlerMessage(message);
                }
            }.start();
        } else if (runMode.equals(RunMode.CURRENT_THREAD)) {
            handlerMessage(message);
        } else if (runMode.equals(RunMode.REUSABLE_THREAD)) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    handlerMessage(message);
                }
            });
        } else if (runMode.equals(RunMode.MAIN_THREAD)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                   handlerMessage(message);
                }
            });
        } else {
            handlerMessage(message);
        }
    }

    @Override
    public ParallelReceiver runOn(RunMode runMode) {
        this.runMode = runMode;
        return this;
    }

    public abstract void handlerMessage(Object message);

    public void start() {
        if(parallelMessgener != null){
          parallelMessageManager.setParallelMessgener(parallelMessgener);
        }else {
            parallelMessageManager.setParallelReceiver(this);
        }
        parallelMessageManager.start();
    }

}
