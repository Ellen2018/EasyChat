package com.ellen.library.library.parallel.commoninterface.parallelmessgener;

import com.ellen.library.library.parallel.ParallelSender;

public abstract class ParallelMessgengrHandler {

    public abstract void handlerMessage(ParallelMessgenerSender parallelMessgenerSender,ParallelSender parallelSender, Object message);

    public abstract void handlerErrMessage(ParallelMessgenerSender parallelMessgenerSender,ParallelSender parallelSender, Throwable throwable);

    public abstract void handlerComplete(ParallelMessgenerSender parallelMessgenerSender,ParallelSender parallelSender,TaskProgress taskProgress,Object message);

    /**
     * 任务完成的进度
     */
    public static class TaskProgress {
        /**
         * 当前的进度
         */
        private int currentProgress;
        /**
         * 总进度
         */
        private int totalProgress;

        public TaskProgress(int currentProgress, int totalProgress) {
            this.currentProgress = currentProgress;
            this.totalProgress = totalProgress;
        }

        public int getCurrentProgress() {
            return currentProgress;
        }

        public void setCurrentProgress(int currentProgress) {
            this.currentProgress = currentProgress;
        }

        public int getTotalProgress() {
            return totalProgress;
        }

        public void setTotalProgress(int totalProgress) {
            this.totalProgress = totalProgress;
        }
    }

}
