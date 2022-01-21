/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2012 - 2013, SnapLogic, Inc.  All rights reserved.
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

import static com.snaplogic.snaps.test.Messages.ERR_FAIL_BOT;
import static com.snaplogic.snaps.test.Messages.FAIL_BOT_SNAP_LABEL;
import static com.snaplogic.snaps.test.Messages.FAIL_BOT_SNAP_PURPOSE;

/**
 * Bad snap that is determined to fail during execution.
 *
 * @author mklumpp
 */
@General(title = FAIL_BOT_SNAP_LABEL, purpose = FAIL_BOT_SNAP_PURPOSE)
@Category(snap = SnapCategory.TRANSFORM)
@Inputs(max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
public class FailBot extends SimpleSnap {

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
    }

    @Override
    protected void process(Document document, String inputViewName) {
        throw new ExecutionException(ERR_FAIL_BOT);
    }

}
