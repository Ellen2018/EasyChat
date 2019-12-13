package com.ellen.easychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ellen.library.easy.Messenger;
import com.ellen.library.easy.Receiver;
import com.ellen.library.easy.Sender;
import com.ellen.library.runmode.RunMode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //上游
        new Sender<Integer>() {

            @Override
            public void handlerInstruction() {
                Log.e("Ellen2018","当前线程名字:"+Thread.currentThread().getName());
                sendToNextMessage(3);
            }
        }
                .runOn(RunMode.REUSABLE_THREAD)
                //中游
                .setMessenger(new Messenger<Integer,String>() {
                    @Override
                    public void handleMessage(Integer receiverMessage) {
                        Log.e("Ellen2018","收到的消息:"+receiverMessage);
                        Log.e("Ellen2018","当前线程名字:"+Thread.currentThread().getName());
                        sendMessageToNext("呵呵");
                    }
                })
                .runOn(RunMode.REUSABLE_THREAD)
                //下游
                .setReceiver(new Receiver<String>() {
                    @Override
                    public void handleMessage(String message) {
                        Log.e("Ellen2018","收到的消息:"+message);
                        Log.e("Ellen2018","当前线程名字:"+Thread.currentThread().getName());
                    }
                })
                .runOn(RunMode.MAIN_THREAD)
                .start();

    }
}
