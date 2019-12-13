> 是否觉得RxJava太难上手呢?不妨使用此库吧，易于理解，易于使用,还能完成RxJava做不到的。这个库的最大好处就是容易理解，并且线程环境易于控制和切换。

## 0.如何导入？

[![](https://jitpack.io/v/Ellen2018/EasyChat.svg)](https://jitpack.io/#Ellen2018/EasyChat)

&emsp;&emsp;首先你需要在项目的build.gradle中配置以下代码：  

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }//加上这句即可
		}
	}

&emsp;&emsp;然后你在要使用该库的module中添加以下依赖:  

     implementation 'com.github.Ellen2018:EasyChat:x.y.z'

&emsp;&emsp;x,y,z是笔者库的版本值，例如：1.0.0

## 1.此库的原理
![EasyChat思维理论](https://oscimg.oschina.net/oscnet/up-9a2e85bc1cdaf4f2222a36d67e5299045d6.png)

## 2.api说明

&emsp;&emsp;先来说明一下三个角色吧，这三个角色分别为:发送者(Sender),拦截者(Messgener),接收者(Receiver),就像上方原理图一样，Sender(上游)->Messgener(中游,可以有0个,1个或者多个)->Receiver(下游),在整个消息系统中，每一个角色都能决定自己处理消息的线程环境,通过runOn方法来控制,所以能取代RxJava也是必须的,hhhh,先来解释解释Sender,Messgener,Receiver三个类的说明，接着演示一下各种场景下的使用:

### Sender说明

&emsp;&emsp;它的泛型指的是它能发送怎样类型的消息,它直接决定handlerInstruction方法中SenderController能发送怎样类型的消息。  
&emsp;&emsp;handlerInstruction方法是Sender专用的处理函数,它的工作环境直接由runOn决定。  
&emsp;&emsp;SenderController是Sender用来向下一级发送消息的控制器对象,handlerInstruction方法中以参数的方式传递过去了，通过SenderController我们可以向下一级发送消息(sendMessageToNext方法)，错误消息(sendErrMessageToNext方法)，完成消息(complete方法)等。  
&emsp;&emsp;详细用法看下面代码示例。

### Messgener说明

&emsp;&emsp;使用它需要两个泛型，第一个泛型参数代表接收怎样类型的消息(注意这里的类型指定要与上级保持一致,不然使用的时候会发生ClassCaseException),第二个泛型参数代表它要发送怎样类型的消息，其实说白了就是拦截者能拦截什么类型的消息和发送什么类型的消息。  
&emsp;&emsp;详细用法看下面代码示例。

### Receiver说明  

&emsp;&emsp;它的泛型指的是它能收到怎样类型的消息(也是与上一级保持一致,不然使用的时候会发生ClassCaseException)。  
&emsp;&emsp;详细用法看下面代码示例。

### 关于三者的runOn方法说明

runOn方法中可以传递一个枚举参数，这个枚举的4种类型如下:

    /**
     * 当前线程模式，与上一级的运行模式保持一致
     */
    CURRENT_THREAD,
    /**
     * 可复用线程模式(使用线程池进行复用)
     */
    REUSABLE_THREAD,
    /**
     * 开启新线程模式
     */
    NEW_THREAD,
    /**
     * 主线程模式(UI线程)
     */
    MAIN_THREAD;

&emsp;&emsp;注意当你不调用runOn方法时，它的默认参数是CURRENT_THREAD，也就是和上一级的线程环境保持一致。

### 场景1:如何像RxJava那样请求网络数据?

&emsp;&emsp;代码示例:

    //这是笔者请求网络视频数据的一段代码(MVP架构) 
    new Sender<VideoBean>(){//这里的VideoBean代表当前Sender发送的消息类型为VideoBean
            @Override
            protected void handlerInstruction(SenderController<VideoBean> senderController) {
               String json = null;
                try {
                    //获取网络Json数据
                    json = mModel.getVideoData();
                } catch (IOException e) {
                    e.printStackTrace();
                    //发送错误消息到下一级
                    senderController.sendErrMessageToNext(e);
                }
                if(json != null){
                    VideoBean videoBean = new Gson().fromJson(json,VideoBean.class);
                    //发送消息到下一级
                    senderController.sendMessageToNext(videoBean);
                }
                senderController.complete();
            }
        }
        //线程环境设置为可复用线程环境
        .runOn(RunMode.REUSABLE_THREAD)
        .setReceiver(new Receiver<VideoBean>() {
            @Override
            protected void handleMessage(VideoBean message) {
              //收到上一级的消息，更新UI
              mView.refreshVideoSuccess(message);
            }

            @Override
            protected void handleErrMessage(Throwable throwable) {
               //收到上一级错误的消息,更新UI
               mView.refreshVideoFailure(throwable.getMessage());
            }

            @Override
            protected void complete() {

            }
        })
        //线程环境设置为UI线程
        .runOn(RunMode.MAIN_THREAD)
        .start();

&emsp;&emsp;以上代码只是一个示范，其实理解起来很简单,首先会调用Sender的handlerInstruction方法，Instruction是指令的意思，就像你给Sender下达一个指令，他就会回调handlerInstruction方法，这个方法调用的线程环境由runOn()方法内传递的参数决定，它的参数是一个枚举，关于这个runOn方法，笔者之后会提到。我们接着来解释以上代码，我们看到代码种handlerInstruction方法种有一个参数SenderController，它是用来干嘛的呢？从名字上就很容易理解，就是发送者控制器，它是用来向下一个级发送消息用的，sendErrMessageToNext是发送错误消息的意思(调用之后下一级的handleErrMessage被调用)，sendMessageToNext是发送消息的意思(调用之后下一级的handleMessage被调用)，complete是完成的意思(调用之后,下一级的complete方法被调用,拦截者没有complete方法，因为笔者再三思考觉得没必要加，以后有时间还是加上吧)，跟RxJava是一摸一样的设计思路。

### 场景2:嵌套请求网络(串行)

&emsp;&emsp;首先,我来解释解释什么是嵌套请求网络，以访有些同学没有遇到这样的需求,有这样一个业务，他需要请求两个接口，只有两个接口全部请求完成了才能更新UI数据,而且第二个接口请求的参数需要第一个接口请求的参数,RxJava似乎做到这点好像不太容易，但是此库就能完全满足这样的需求，只不过是串行的方式(要想并行的方式，需要笔者再进行封装)，我们接下来就来一个简单的例子，比如我想把一个Byte类型的数据转化为Integer，然后再转化为String类型，正好类似上面那种嵌套网络需求，代码示例如下:

        final byte data = 3;
        new Sender<Integer>(){
            @Override
            protected void handlerInstruction(SenderController<Integer> senderController) {
                Integer data1 = new Integer(data);
                senderController.sendMessageToNext(data1);
            }
        }
        //指定Sender工作环境
        .runOn(RunMode.REUSABLE_THREAD)
        .setMessenger(new Messenger<Integer,String>() {
            @Override
            protected void handleMessage(MessengerSender messengerSender, Integer receiverMessage) {
                 String s = String.valueOf(receiverMessage);
                 //发送消息给下一级
                 messengerSender.sendMessageToNext(s);
            }

            @Override
            protected void handleErrMessage(MessengerSender messengerSender, Throwable throwable) {

            }
        })
        //指定Messenger的工作环境
        .runOn(RunMode.REUSABLE_THREAD)
        .setReceiver(new Receiver<String>() {
            @Override
            protected void handleMessage(String message) {
                Log.e("Ellen2018","收到消息:"+message);
            }

            @Override
            protected void handleErrMessage(Throwable throwable) {

            }

            @Override
            protected void complete() {

            }
        })
        //指定Receiver的工作环境 
        .runOn(RunMode.MAIN_THREAD)
        .start();

### 场景3:需要请求完3个接口才能更新UI(并行)

&emsp;&emsp;这部分待笔者封装api再写
