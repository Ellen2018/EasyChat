package com.ellen.library.easyinterface;

import com.ellen.library.runmode.RunMode;

public interface ThreadRunMode<T> {
    T runOn(RunMode runMode);
}
