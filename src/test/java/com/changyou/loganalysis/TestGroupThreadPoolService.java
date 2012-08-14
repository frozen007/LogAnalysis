package com.changyou.loganalysis;

import com.changyou.concurrent.GroupThreadPoolService;

import junit.framework.TestCase;

public class TestGroupThreadPoolService extends TestCase {
    public static void main(String[] args) {
        new TestGroupThreadPoolService().test001();
    }

    public void test001() {

        GroupThreadPoolService<String> serve = new GroupThreadPoolService<String>(1) {

            @Override
            protected void processSingle(String e) {
                System.out.println(e);

            }
        };

        serve.start();
        System.out.println("Begins");
        for (int i = 0; i < 1000; i++) {
            serve.dispatch("counter"+i);
            //System.out.println("serve.dispatch-counter"+i);
        }
        //serve.waitForFinish();
        System.out.println("Finished");

    }

}
