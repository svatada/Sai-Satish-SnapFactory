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
import com.snaplogic.api.Lint;
import com.snaplogic.api.Notification;
import com.snaplogic.api.Notifications;
import com.snaplogic.api.Snap;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;

/**
 * A snap that reports a lint.
 *
 * @author tstack
 */
@General(title = "Warn Bot", purpose = "Report a warning")
@Category(snap = SnapCategory.WRITE)
@Inputs(max = 0, accepts = {ViewType.DOCUMENT})
@Outputs(max = 0)
@Version(snap = 1)
public class WarnBot implements Snap {

    @Notification(
            message = "This is an example lint",
            reason = "Because",
            resolution = "Don't use this snap"
    )
    private static final Lint EXAMPLE_LINT = new Lint();

    static {
        Notifications.register(WarnBot.class);
    }

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
    }

    @Override
    public void execute() throws ExecutionException {
        EXAMPLE_LINT.report();
    }

    @Override
    public void cleanup() throws ExecutionException {
    }
}
