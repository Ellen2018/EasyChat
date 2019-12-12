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
        new Sender<String>("ss") {
            @Override
            public void handlerInstruction(String s) {
                Log.e("Ellen20181","收到消息:"+s);
                Log.e("Ellen2018","线程名字:"+Thread.currentThread().getName());
                sendToNextMessage(s);
            }
        }
                .runOn(RunMode.IO)
                //下游
                .setReceiver(new Receiver() {
                    @Override
                    public void receiverMessage(Object receiverMessage) {
                        Log.e("Ellen20183","收到消息:"+receiverMessage);
                        Log.e("Ellen2018","线程名字:"+Thread.currentThread().getName());
                    }

                }).runOn(RunMode.MAIN_THREAD)
                .start();

    }
}
