/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2016, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */
package com.snaplogic.snaps.secsnappack;

import com.snaplogic.snap.test.harness.SnapTestRunner;
import com.snaplogic.snap.test.harness.TestFixture;
import com.snaplogic.snap.test.harness.TestResult;
import com.snaplogic.snap.test.harness.TestSetup;

import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests that the {@link DocConsumer} Snap consumer all input Documents.
 */
@RunWith(SnapTestRunner.class)
public class DocConsumerTest {

    // a TestSetup argument allows interacting with the test before triggering it
    @TestFixture(snap = DocConsumer.class)
    public void docConsumer_WithManualTestSetup_UpdatesCounterCorrectly(TestSetup testSetup)
            throws Exception {
        testSetup.addInputView("input0", Arrays.<Object>asList("1", "2"));

        // This demonstrates how get a reference to a private field within a Snap using TestSetup
        AtomicInteger count = new AtomicInteger(2);
        testSetup.inject().fieldName("count").dependency(count).add();

        TestResult testResult = testSetup.test(); // test the Snap by running through its lifecycle
        assertThat(4, equalTo(count.get()));
    }
}