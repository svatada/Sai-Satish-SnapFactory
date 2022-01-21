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

import com.snaplogic.snap.test.harness.SnapTestRunner;
import com.snaplogic.snap.test.harness.TestFixture;

import org.junit.runner.RunWith;

/**
 * @author mklumpp
 */
@SuppressWarnings("nls")
@RunWith(SnapTestRunner.class)
public class ErrorBotTest {

    @TestFixture(snap = ErrorBot.class,
            input = "data/errorbot_input.json",
            errors = "err",
            expectedErrorPath = "data/expected")
    public void testWriteError() {
    }
}
