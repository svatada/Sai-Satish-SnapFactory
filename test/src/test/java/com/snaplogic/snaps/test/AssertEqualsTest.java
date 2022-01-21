/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2014, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */
package com.snaplogic.snaps.test;

import com.snaplogic.api.ExecutionException;
import com.snaplogic.snap.test.harness.SnapTestRunner;
import com.snaplogic.snap.test.harness.TestFixture;

import org.junit.runner.RunWith;

/**
 * Unit tests for AssertEquals Snap
 *
 * @author psung
 */
@RunWith(SnapTestRunner.class)
public class AssertEqualsTest {

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_input_1.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/assertequals_properties_1.json")
    public void testFlow() {
    }

    @SuppressWarnings("unchecked")
    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_input_2.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/assertequals_properties_1.json")
    public void testNotEqual() {
    }

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_input_3.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/assertequals_properties_3.json")
    public void testEquals() {
    }

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_input_3.json",
            outputs = "out", errors = "err",
            properties = "data/assertequals_properties_withouteval.json",
            exception = ExecutionException.class)
    public void testEqualsWithoutEvalNegativeParsingException() {
    }

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_properties_with_variable_as_stringdata.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/assertequals_properties_witheval_escaped_variable.json")
    public void testEqualsWithEvalEscapedVariable() {
    }

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_properties_with_variable_as_stringdata.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/assertequals_properties_witheval_escaped_variable.json")
    public void testEqualsWithoutEvalContentWithDollarSign() {
    }

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_input_3.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/assertequals_properties_witheval.json")
    public void testEqualsWithEval() {
    }

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_input_empty.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/assertequals_properties_empty_data.json")
    public void testEmptyDocument() {
    }

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_input_empty.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            properties = "data/assertequals_properties_empty_child_data.json")
    public void testEmptyChildDocument() {
    }

    @TestFixture(snap = AssertEquals.class,
            input = "data/assertequals_input_empty.json",
            outputs = "out", errors = "err",
            expectedOutputPath = "data/expected",
            expectedErrorPath = "data/expected",
            exception = ExecutionException.class,
            properties = "data/assertequals_properties_1.json")
    public void testWithIncorrectExpectedDocuments() {
    }
}
