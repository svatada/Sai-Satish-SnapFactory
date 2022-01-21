/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2013, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */

package com.snaplogic.snaps.test;

import com.snaplogic.snap.test.harness.SnapTestRunner;
import com.snaplogic.snap.test.harness.TestFixture;
import com.snaplogic.snap.test.harness.TestSetup;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * Unit test for throttle bot.
 *
 * @author ksubramanian
 */
@SuppressWarnings("nls")
@RunWith(SnapTestRunner.class)
public class ThrottleBotTest {

    private static final int NO_OF_DOCS = 3;
    private static final long WAIT_TIME_PER_DOCUMENT = 1000;
    private static final int EXP_CONSTANT = 100;
    private static final int LOG_CONSTANT = 1000;
    private static final long MAX_RANDOM_PER_DOC = 1000;

    @TestFixture(snap = ThrottleBot.class,
            properties = "data/throttlebot_properties_1.json",
            input = "data/throttlebot_input.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected")
    public void testRandomThrottle(TestSetup testSetup) throws Exception {
        long startTime = System.currentTimeMillis();
        testSetup.test();
        long endTime = System.currentTimeMillis();
        Assert.assertThat("duration", (endTime - startTime),
                Matchers.lessThanOrEqualTo(NO_OF_DOCS * MAX_RANDOM_PER_DOC));
    }

    @TestFixture(snap = ThrottleBot.class,
            properties = "data/throttlebot_properties_2.json",
            input = "data/throttlebot_input.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected")
    public void testFixedThrottle(TestSetup testSetup) throws Exception {
        long startTime = System.currentTimeMillis();
        testSetup.test();
        long endTime = System.currentTimeMillis();
        Assert.assertThat("duration", (endTime - startTime), Matchers.greaterThanOrEqualTo(
                (long) NO_OF_DOCS * WAIT_TIME_PER_DOCUMENT));
    }

    @TestFixture(snap = ThrottleBot.class,
            properties = "data/throttlebot_properties_3.json",
            input = "data/throttlebot_input.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected")
    public void testExponentialThrottle(TestSetup testSetup) throws Exception {
        long startTime = System.currentTimeMillis();
        testSetup.test();
        long endTime = System.currentTimeMillis();
        Assert.assertThat("duration", (endTime - startTime), Matchers.greaterThanOrEqualTo(
                getExponentialValueFor(NO_OF_DOCS)));
    }

    @TestFixture(snap = ThrottleBot.class,
            properties = "data/throttlebot_properties_4.json",
            input = "data/throttlebot_input.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected")
    public void testLogarithmicThrottle(TestSetup testSetup) throws Exception {
        long startTime = System.currentTimeMillis();
        testSetup.test();
        long endTime = System.currentTimeMillis();
        Assert.assertThat("duration", (endTime - startTime), Matchers.greaterThanOrEqualTo(
                getLogarithmicValueFor(NO_OF_DOCS)));
    }

    private long getExponentialValueFor(final int noOfDocs) {
        double value = 0;
        for (int i = 0; i < noOfDocs; i++) {
            value += (EXP_CONSTANT * (int) Math.exp(i + 1));
        }
        return (long) value;
    }

    private long getLogarithmicValueFor(final int noOfDocs) {
        double value = 0;
        for (int i = 0; i < noOfDocs; i++) {
            value += (LOG_CONSTANT * (int) Math.log(i + 1));
        }
        return (long) value;
    }
}
