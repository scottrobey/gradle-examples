package org.sample.kotlinexample;

import org.junit.Test;

public class MyTestClazz {

    @Test
    public void testProduction() throws Exception {
        MyProductionClazz productionClazzUnderTest = new MyProductionClazz();

        productionClazzUnderTest.work();

    }
}
