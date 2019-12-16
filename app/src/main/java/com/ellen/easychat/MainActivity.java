package com.ellen.easychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ellen.library.library.parallel.ParallelMessageManager;
import com.ellen.library.library.parallel.ParallelMessgener;
import com.ellen.library.library.parallel.ParallelReceiver;
import com.ellen.library.library.parallel.ParallelSender;
import com.ellen.library.library.parallel.commoninterface.parallelmessgener.ParallelMessgenerSender;
import com.ellen.library.library.parallel.commoninterface.parallelmessgener.ParallelMessgengrHandler;
import com.ellen.library.library.parallel.commoninterface.parallelsender.ParallelSenderControl;
import com.ellen.library.library.runmode.RunMode;
import com.ellen.library.library.serial.Messenger;
import com.ellen.library.library.serial.Receiver;
import com.ellen.library.library.serial.Sender;
import com.ellen.library.library.serial.commoninterface.messenger.MessengerSender;
import com.ellen.library.library.serial.commoninterface.sender.SenderController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test1();
    }

    /**
     * 仿RxJava流式调用
     */
    public void test() {
        final String s = "32";
        new Sender<Integer>() {
            @Override
            protected void handlerInstruction(SenderController<Integer> senderController) {
                senderController.sendMessageToNext(Integer.valueOf(s));
            }
        }
                .runOn(RunMode.REUSABLE_THREAD)
                .setMessenger(new Messenger<Integer, String>() {
                    @Override
                    protected void handleMessage(MessengerSender<String> messengerSender, Integer receiverMessage) {
                        messengerSender.sendMessageToNext(String.valueOf(receiverMessage));
                    }

                    @Override
                    protected void handleErrMessage(MessengerSender<String> messengerSender, Throwable throwable) {

                    }
                }).runOn(RunMode.NEW_THREAD)
                .setMessenger(new Messenger<String, Integer>() {
                    @Override
                    protected void handleMessage(MessengerSender<Integer> messengerSender, String receiverMessage) {
                        messengerSender.sendMessageToNext(Integer.valueOf(receiverMessage));
                    }

                    @Override
                    protected void handleErrMessage(MessengerSender<Integer> messengerSender, Throwable throwable) {

                    }
                }).runOn(RunMode.NEW_THREAD)
                .setReceiver(new Receiver<Integer>() {
                    @Override
                    protected void handleMessage(Integer message) {
                        Log.e("Ellen2018", "收到消息:" + message);
                    }

                    @Override
                    protected void handleErrMessage(Throwable throwable) {

                    }

                    @Override
                    protected void complete() {

                    }
                }).runOn(RunMode.MAIN_THREAD).start();

    }

    /**
     * 并行需求
     */
    public void test1() {
        new ParallelMessageManager().addParallelSender(new ParallelSender() {
            @Override
            protected void handlerInstruction(ParallelSenderControl senderControl) {
                senderControl.sendMessageToNext("3");
                senderControl.sendCompleteMessage("完成");
            }
        }).addParallelSender(new ParallelSender() {
            @Override
            protected void handlerInstruction(ParallelSenderControl senderControl) {
                senderControl.sendMessageToNext("4");
                senderControl.sendCompleteMessage("呵呵哒");
            }
        }).setParallelReceiver(new ParallelReceiver() {
            @Override
            public void handleMessage(ParallelSender parallelSender, Object message) {
                Log.e("Ellen2018", "线程工作环境-接收者:" + Thread.currentThread().getName());
                Log.e("Ellen2018", "收到的消息(来自于" + parallelSender.getTag() + "):" + message);
            }

            @Override
            public void handleErrMessage(ParallelSender parallelSender, Throwable throwable) {
                Log.e("Ellen2018", "线程工作环境-接收者:" + Thread.currentThread().getName());
                Log.e("Ellen2018", "收到错误消息(来自于" + parallelSender.getTag() + "):" + throwable.getMessage());
            }

            @Override
            public void handleComplete(ParallelSender parallelSender, ParallelMessgengrHandler.TaskProgress taskProgress, Object message) {
                Log.e("Ellen2018", "线程工作环境-接收者:" + Thread.currentThread().getName());
                Log.e("Ellen2018", "收到完成的消息(来自于" + parallelSender.getTag() + "):"+message);
                Log.e("Ellen2018", "任务进度是:"+taskProgress.getCurrentProgress()+"/"+taskProgress.getTotalProgress());
            }
        }).runOn(RunMode.MAIN_THREAD).start();
    }
}
