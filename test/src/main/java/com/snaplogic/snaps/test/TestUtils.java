/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2020, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */
package com.snaplogic.snaps.test;

import com.snaplogic.snap.api.InputViews;
import com.snaplogic.snap.view.InputView;

/**
 * Helper methods for the Test Snap Pack
 *
 */
public class TestUtils {

    /**
     * Checks if there are one or more input documents from any input view.
     *
     * @param inputViews - InputViews object containing info on all input views
     *
     * @return true if there is at least one input document from any input view
     */
    public static boolean hasInputDocuments(InputViews inputViews) {
        if (!inputViews.isEmpty()) {
            for (InputView view : inputViews) {
                if (inputViews.getDocumentsFrom(view).hasNext()) {
                    return true;
                }
            }
        }
        return false;
    }
}
