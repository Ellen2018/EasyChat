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
        new ParallelMessageManager()
                .addParallelSender(new ParallelSender("任务1") {
            @Override
            protected void handlerInstruction(ParallelSenderControl senderControl) {
                //请求接口1..
                String json1 = "网络接口1请求的数据";
                senderControl.sendCompleteMessage(json1);
            }
        }).setReTryTime(5)//可以设置某个任务错误时重试的次数，是不是很人性化呢，哈哈哈
                .addParallelSender(new ParallelSender("任务2") {
            @Override
            protected void handlerInstruction(ParallelSenderControl senderControl) {
                //请求接口2..
                String json2 = "网络接口2请求的数据";
                senderControl.sendCompleteMessage(json2);
            }
        }).addParallelSender(new ParallelSender("任务3") {
            @Override
            protected void handlerInstruction(ParallelSenderControl senderControl) {
                //请求接口3..
                String json3 = "网络接口3请求的数据";
                senderControl.sendCompleteMessage(json3);
            }
        }).setParallelReceiver(new ParallelReceiver() {
            @Override
            public void handleMessage(ParallelSender parallelSender, Object message) {
              //上游每发送一条消息都会调用此方法
            }

            @Override
            public void handleErrMessage(ParallelSender parallelSender, Throwable throwable) {
                //上游每发送一条错误消息都会调用此方法
            }

            @Override
            public void handleComplete(ParallelSender parallelSender, ParallelMessgengrHandler.TaskProgress taskProgress, Object message) {
                //上游每发送一条消息都会调用此方法

                //1.如何获取请求的进度
                String jinDu = taskProgress.getCurrentProgress()+"/"+taskProgress.getTotalProgress();

                //2.如何获取发送过来的消息并处理
                String json = (String) message;
                //注意这里区分tag
                if(parallelSender.getTag().equals("任务1")){
                    //任务1成功请求到Json的逻辑
                }else if(parallelSender.getTag().equals("任务2")){
                    //任务2成功请求到Json的逻辑
                }else if(parallelSender.getTag().equals("任务3")){
                    //任务3成功请求到Json的逻辑
                }
            }
        })
                //使Receiver的处理全部在主线程(UI线程)
                .runOn(RunMode.MAIN_THREAD)
                //千万别忘记调用start
                .start();
    }
}
