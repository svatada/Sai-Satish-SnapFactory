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

import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.test.harness.OutputRecorder;
import com.snaplogic.snap.test.harness.SnapTestRunner;
import com.snaplogic.snap.test.harness.TestFixture;
import com.snaplogic.snap.test.harness.TestResult;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author mklumpp, ksubramanian
 */
@SuppressWarnings("nls")
@RunWith(SnapTestRunner.class)
public class SourceBotTest {

    @TestFixture(snap = SourceBot.class,
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/sourcebot_properties_1.json")
    public void testSink() {
    }

    @TestFixture(snap = SourceBot.class,
            outputs = "out", errors = "err",
            expectedErrorPath = "data/expected",
            properties = "data/sourcebot_properties_2.json")
    public void testSourceDate(TestResult testResult) throws Exception {
        LocalDate date = new LocalDate(2012, 11, 5);
        OutputRecorder outputRecorder = testResult.getOutputViews().iterator().next();
        for (Document doc : outputRecorder.getRecordedDocuments()) {
            Map<String, Object> map = doc.get(Map.class);
            LocalDate value = (LocalDate) map.get("0");
            assertEquals(date, value);
        }
    }

    @TestFixture(snap = SourceBot.class,
            outputs = "out", errors = "err",
            expectedErrorPath = "data/expected",
            properties = "data/sourcebot_properties_3.json")
    public void testSourceDateTime(TestResult testResult) throws Exception {
        final DateTime dt = new DateTime(2012, 11, 5, 14, 21, 45, 0, DateTimeZone.UTC);
        OutputRecorder outputRecorder = testResult.getOutputViews().iterator().next();
        for (Document doc : outputRecorder.getRecordedDocuments()) {
            Map<String, Object> map = doc.get(Map.class);
            DateTime value = (DateTime) map.get("0");
            assertEquals(dt, value.toMutableDateTime(DateTimeZone.UTC));
        }
    }

    @TestFixture(snap = SourceBot.class,
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/sourcebot_properties_4.json")
    public void testSourceFloat() {
    }
}
