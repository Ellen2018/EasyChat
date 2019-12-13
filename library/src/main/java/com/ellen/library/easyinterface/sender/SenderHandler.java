package com.ellen.library.easyinterface.sender;

public abstract class SenderHandler<T> {
    /**
     * 处理来自于主人的指令
     */
    protected abstract void handlerInstruction(SenderController<T> senderController);
}
