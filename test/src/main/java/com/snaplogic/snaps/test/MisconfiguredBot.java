/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2019, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */

package com.snaplogic.snaps.test;

import com.google.inject.Inject;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.OutputViews;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A snap that throws an exception during configuration
 */
@General(title = "Misconfigured Bot", purpose = "Test when configure throws an except cleanup still occurs")
@Category(snap = SnapCategory.TRANSFORM)
@Inputs(max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(max = 1)
@Version(snap = 1)
public class MisconfiguredBot extends SimpleSnap {

    private static final Logger log = LoggerFactory.getLogger(MisconfiguredBot.class);

    @Override
    public void process(Document document, String inputViewName) {
        throw new ExecutionException(Messages.MISCONFIGURED_BOT);
    }

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ExecutionException {
        log.info(Messages.MISCONFIGURED_BOT);
        throw new ExecutionException(Messages.MISCONFIGURED_BOT);
    }

    @Override
    public void cleanup() throws ExecutionException {
        log.info("MisconfiguredBot has cleaned up");
    }
}
