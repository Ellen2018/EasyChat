package com.ellen.library.library.parallel;

import android.os.Handler;
import android.util.Log;

import com.ellen.library.library.parallel.commoninterface.parallelsender.ParallelSenderControl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 能完成并行需求
 */
public abstract class ParallelSender {

    /**
     * 拦截者
     */
    private ParallelMessgener parallelMessgener;
    /**
     *
     */
    private ParallelReceiver parallelReceiver;
    private List<ParallelSender> parallelSendersList;
    private ParallelMessageManager parallelMessageManager;
    private Handler handler = new Handler();
    /**
     * 标记(方便Receiver分辨谁完成了任务)
     */
    private String tag;
    /**
     * 设置失败之后重试的次数
     */
    private int reTryTime = 0;

    /**
     * 记录当前retry的次数
     * @param tag
     */
    private int currentRetryTimes = 0;
    private ExecutorService retryExecutorService = Executors.newFixedThreadPool(1);
    private ParallelSenderControl senderControl;

    public ParallelSender(String tag){
        this.tag = tag;
        initSenderControl();
    }

    public ParallelSender(){
        initSenderControl();
    }

    ParallelSenderControl getSenderControl(){
        return senderControl;
    }

    private void initSenderControl(){
        senderControl = new ParallelSenderControl() {
            @Override
            public void sendMessageToNext(Object o) {
                if (parallelMessgener != null) {
                    parallelMessgener.receiverMessage(ParallelSender.this,o);
                }
                if (parallelReceiver != null) {
                    parallelReceiver.receiverMessage(ParallelSender.this,o);
                }
            }

            @Override
            public void sendErrMessageToNext(Throwable throwable) {
                if(reTryTime != 0){
                    if(currentRetryTimes == reTryTime){
                        //结束重试，并发消息给下一级任务失败
                        if(parallelMessgener != null){
                            parallelMessgener.receiverErrMessage(ParallelSender.this,throwable);
                        }
                        if(parallelReceiver != null){
                            parallelReceiver.receiverErrMessage(ParallelSender.this,throwable);
                        }
                    }else {
                        currentRetryTimes++;
                        Log.e("Ellen2018",tag+"重试次数:"+currentRetryTimes);
                        //再次提交任务
                        retryExecutorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                ParallelSender.this.handlerInstruction(senderControl);
                            }
                        });
                    }
                }else {
                    //直接发送错误消息给下一级任务失败
                    if(parallelMessgener != null){
                        parallelMessgener.receiverErrMessage(ParallelSender.this,throwable);
                    }
                    if(parallelReceiver != null){
                        parallelReceiver.receiverErrMessage(ParallelSender.this,throwable);
                    }
                }
            }

            @Override
            public void sendCompleteMessage(Object message) {
                if(parallelMessgener != null){
                  parallelMessgener.receiverComplete(ParallelSender.this,message);
                }
                //这里暂时不加
                if(parallelReceiver != null){
                   parallelReceiver.receiverCompleteFromSender(ParallelSender.this,message);
                }
            }
        };
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    void setParallelSendersList(List<ParallelSender> parallelSendersList) {
        this.parallelSendersList = parallelSendersList;
    }

    public ParallelSender addParallelSender(ParallelSender parallelSender) {
        parallelSendersList.add(parallelSender);
        if(parallelSender.getTag() == null){
            parallelSender.setTag("default-task-"+parallelSendersList.size());
        }
        parallelSender.setParallelSendersList(parallelSendersList);
        parallelSender.setParallelMessageManager(parallelMessageManager);
        return parallelSender;
    }

    public ParallelMessgener setParallelMessgener(ParallelMessgener parallelMessgener) {
        this.parallelMessgener = parallelMessgener;
        this.parallelMessgener.setParallelMessageManager(parallelMessageManager);
        this.parallelMessgener.setParallelSendersList(parallelSendersList);
        this.parallelMessgener.setHandler(handler);
        return this.parallelMessgener;
    }

    public ParallelReceiver setParallelReceiver(ParallelReceiver parallelReceiver) {
        this.parallelReceiver = parallelReceiver;
        this.parallelReceiver.setParallelMessageManager(parallelMessageManager);
        this.parallelReceiver.setParallelSendersList(parallelSendersList);
        this.parallelReceiver.setHandler(handler);
        return this.parallelReceiver;
    }

    void setParallelMessageManager(ParallelMessageManager parallelMessageManager) {
        this.parallelMessageManager = parallelMessageManager;
    }

    void setNull(){
        parallelMessgener = null;
        parallelReceiver = null;
    }

    void reSet(ParallelMessgener parallelMessgener,ParallelReceiver parallelReceiver){
        this.parallelMessgener = parallelMessgener;
        this.parallelReceiver = parallelReceiver;
    }

    protected abstract void handlerInstruction(ParallelSenderControl senderControl);

    public ParallelSender setReTryTime(int reTryTime) {
        this.reTryTime = reTryTime;
        return this;
    }
}
