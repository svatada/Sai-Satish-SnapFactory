/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2017, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */

package com.snaplogic.snaps.test;

import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;

/**
 * A snap that fails to initialize.
 */
@General(title = "Link Error Bot", purpose = "Fails to load a native library")
@Category(snap = SnapCategory.WRITE)
@Inputs(max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(max = 0)
@Version(snap = 1)
public class LinkErrorBot extends SimpleSnap {

    static {
        if (System.getProperty("maven.home") == null) {
            System.loadLibrary("badlibname");
        }
    }

    @Override
    public void process(Document document, String inputViewName) {
    }

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
    }

    @Override
    public void cleanup() throws ExecutionException {
    }
}
