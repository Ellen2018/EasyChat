package com.ellen.easychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ellen.library.easy.Messenger;
import com.ellen.library.easy.Receiver;
import com.ellen.library.easy.Sender;
import com.ellen.library.easyinterface.messenger.MessengerSender;
import com.ellen.library.easyinterface.sender.SenderController;
import com.ellen.library.parallel.ParallelMessageManager;
import com.ellen.library.parallel.ParallelMessgener;
import com.ellen.library.parallel.ParallelReceiver;
import com.ellen.library.parallel.ParallelSender;
import com.ellen.library.runmode.RunMode;

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

    /**
     * 并行需求
     */
    public void test1(){
       new ParallelMessageManager()
               .addParallelSender(new ParallelSender() {
                   @Override
                   protected void handlerInstruction() {
                      sendMessageToNext("3");
                   }
               }).addParallelSender(new ParallelSender("任务n") {
           @Override
           protected void handlerInstruction() {
               //sendMessageToNext("4");
               sendErrMessageToNext(new Throwable("网络问题"));
           }
       }.setReTryTime(8)).addParallelSender(new ParallelSender() {
           @Override
           protected void handlerInstruction() {
               sendMessageToNext("5");
           }
       }).setParallelMessgener(new ParallelMessgener() {
           @Override
           public void handlerMessage(String tag, Object message) {

           }

           @Override
           public void handlerMessage(int currentWanChen, int allCount, String tag, Object message) {
               Log.e("Ellen2018-拦截者","线程环境:"+Thread.currentThread().getName());
               String jinDu = currentWanChen+"/"+allCount;
               Log.e("Ellen2018-拦截者",jinDu+":"+message);
               sendMessage(message);
           }

           @Override
           public void handlerErrMessage(ParallelSender parallelSender, Throwable throwable) {
               Log.e("Ellen2018-拦截者","线程环境:"+Thread.currentThread().getName());
                Log.e("Ellen2018-拦截者",parallelSender.getTag()+"出现问题:"+throwable.getMessage());
                sendErrMessage(parallelSender,throwable);
           }
       }).setParallelReceiver(new ParallelReceiver() {
           @Override
           public void handlerMessage(Object message) {
               Log.e("Ellen2018-接收者","线程环境:"+Thread.currentThread().getName());
               Log.e("Ellen2018-接收者","消息:"+message);
           }

           @Override
           public void handleErrMessage(ParallelSender parallelSender, Throwable throwable) {
               Log.e("Ellen2018-接收者","线程环境:"+Thread.currentThread().getName());
               Log.e("Ellen2018-接收者",parallelSender.getTag()+"出现问题:"+throwable.getMessage());
           }
       }).runOn(RunMode.MAIN_THREAD).start();
    }
}
