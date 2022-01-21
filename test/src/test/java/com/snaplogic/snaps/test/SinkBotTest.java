/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2012, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */

package com.snaplogic.snaps.test;

import com.snaplogic.api.Snap;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.test.harness.SnapTestRunner;
import com.snaplogic.snap.test.harness.TestFixture;
import com.snaplogic.snap.test.harness.TestResult;

import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author mklumpp
 */
@SuppressWarnings("nls")
@RunWith(SnapTestRunner.class)
public class SinkBotTest {

    public static class MockSinkBot extends SinkBot {

        private int counter = 0;

        @Override
        public void process(Document document, String inputViewName) {
            counter++;
            super.process(document, inputViewName);
        }

        int getCount() {
            return counter;
        }
    }

    @TestFixture(snap = MockSinkBot.class,
            properties = "data/sinkbot_properties_1.json",
            input = "data/sinkbot_input.json",
            errors = "err",
            expectedErrorPath = "data/expected")
    public void testSinkBot(TestResult testResult) {
        assertEquals(2, ((MockSinkBot) testResult.getSnap()).getCount());
    }
}
