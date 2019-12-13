package com.ellen.easychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ellen.library.easy.Messenger;
import com.ellen.library.easy.Receiver;
import com.ellen.library.easy.Sender;
import com.ellen.library.easyinterface.messenger.MessengerSender;
import com.ellen.library.easyinterface.sender.SenderController;
import com.ellen.library.runmode.RunMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //上游
        new Sender<String>() {
            @Override
            public void handlerInstruction(SenderController<String> senderController) {
                Log.e("Ellen2018", "工作的线程-发送者:" + Thread.currentThread().getName());
                senderController.sendErrMessageToNext(new Throwable("网络错误"));
                senderController.sendMessageToNext("hello");
                senderController.complete();
            }
        }
                .runOn(RunMode.REUSABLE_THREAD)
                .setMessenger(new Messenger() {
                    @Override
                    public void handleMessage(MessengerSender messengerSender, Object receiverMessage) {
                        Log.e("Ellen2018", "工作的线程-拦截者1:" + Thread.currentThread().getName());
                        Log.e("Ellen2018","收到的消息-拦截者1:"+receiverMessage);
                        messengerSender.sendMessageToNext(receiverMessage);
                    }

                    @Override
                    public void handleErrMessage(MessengerSender messengerSender, Throwable throwable) {
                        Log.e("Ellen2018", "工作的线程-拦截者1:" + Thread.currentThread().getName());
                        Log.e("Ellen2018","收到的错误消息-拦截者1:"+throwable);
                        messengerSender.sendErrMessageToNext(throwable);
                    }

                })
                .setMessenger(new Messenger() {
                    @Override
                    public void handleMessage(MessengerSender messengerSender, Object receiverMessage) {
                        Log.e("Ellen2018", "工作的线程-拦截者2:" + Thread.currentThread().getName());
                        Log.e("Ellen2018","收到的消息-拦截者2:"+receiverMessage);
                        messengerSender.sendMessageToNext(receiverMessage);
                    }

                    @Override
                    public void handleErrMessage(MessengerSender messengerSender, Throwable throwable) {
                        Log.e("Ellen2018", "工作的线程-拦截者2:" + Thread.currentThread().getName());
                        Log.e("Ellen2018","收到的错误消息-拦截者2:"+throwable);
                        messengerSender.sendErrMessageToNext(throwable);
                    }
                })
                .setReceiver(new Receiver<String>() {
                    @Override
                    public void handleMessage(String message) {
                        Log.e("Ellen2018", "工作的线程-接收者:" + Thread.currentThread().getName());
                        Log.e("Ellen2018", "接收的信息-接收者:" + message);
                    }

                    @Override
                    public void handleErrMessage(Throwable throwable) {
                        Log.e("Ellen2018", "工作的线程-接收者:" + Thread.currentThread().getName());
                        Log.e("Ellen2018", "错误信息-接收者:" + throwable.getMessage());
                    }

                    @Override
                    public void complete() {
                        Log.e("Ellen2018", "工作的线程-接收者:" + Thread.currentThread().getName());
                        Log.e("Ellen2018", "完成-接收者");
                    }
                })
                .runOn(RunMode.MAIN_THREAD).start();
    }
}
