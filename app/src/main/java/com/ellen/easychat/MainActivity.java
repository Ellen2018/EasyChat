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
        test();
    }

    public void test(){
        final String s = "32";
        new Sender<Integer>(){
            @Override
            protected void handlerInstruction(SenderController<Integer> senderController) {
                senderController.sendMessageToNext(Integer.valueOf(s));
            }
        }
        .runOn(RunMode.REUSABLE_THREAD)
        .setMessenger(new Messenger<Integer,String>() {
            @Override
            protected void handleMessage(MessengerSender<String> messengerSender, Integer receiverMessage) {
                messengerSender.sendMessageToNext(String.valueOf(receiverMessage));
            }

            @Override
            protected void handleErrMessage(MessengerSender<String> messengerSender, Throwable throwable) {

            }
        }).runOn(RunMode.NEW_THREAD)
                .setMessenger(new Messenger<String,Integer>() {
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
                Log.e("Ellen2018","收到消息:"+message);
            }

            @Override
            protected void handleErrMessage(Throwable throwable) {

            }

            @Override
            protected void complete() {

            }
        }).runOn(RunMode.MAIN_THREAD).start();

    }
}
