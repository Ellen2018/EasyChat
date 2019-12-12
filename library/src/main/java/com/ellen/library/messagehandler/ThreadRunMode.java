package com.ellen.library.messagehandler;

import com.ellen.library.runmode.RunMode;

public interface ThreadRunMode<T> {
    T runOn(RunMode runMode);
}
