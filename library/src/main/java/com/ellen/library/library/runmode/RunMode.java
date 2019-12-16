package com.ellen.library.library.runmode;

public enum RunMode {
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
}
