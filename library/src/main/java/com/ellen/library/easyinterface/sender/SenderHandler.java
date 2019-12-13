package com.ellen.library.easyinterface.sender;

public interface SenderHandler<T> {
    /**
     * 处理来自于主人的指令
     */
    void handlerInstruction(SenderController<T> senderController);
}
