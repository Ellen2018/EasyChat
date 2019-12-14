package com.ellen.library.easyinterface.parallel;

import com.ellen.library.easy.Sender;
import com.ellen.library.parallel.ParallelSender;

public interface ParallelControl {

    ParallelSender addSenders(Sender sender);

}
