package com.ellen.library.library.runmode;

public interface ThreadRunMode<T> {
    T runOn(RunMode runMode);
}
