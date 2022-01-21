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

import com.google.inject.Inject;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.api.Notification;
import com.snaplogic.api.StatusMessage;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.SnapDataException;
import com.snaplogic.snap.api.StatusReporter;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.snaplogic.snaps.test.Messages.DEBUG_ERROR_BOT;
import static com.snaplogic.snaps.test.Messages.ERROR_BOT_LABEL;
import static com.snaplogic.snaps.test.Messages.ERROR_BOT_PURPOSE;
import static com.snaplogic.snaps.test.Messages.ERR_ERROR_BOT;

/**
 * A 'error' snap will simply write the all Documents passed in to the error view.
 *
 * @author ksubramanian
 */
@General(title = ERROR_BOT_LABEL, purpose = ERROR_BOT_PURPOSE)
@Category(snap = SnapCategory.WRITE)
@Inputs(max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(max = 0)
@Version(snap = 1)
public class ErrorBot extends SimpleSnap {
    private static final Logger log = LoggerFactory.getLogger(ErrorBot.class);

    @Notification(message = "Hello, World!")
    private static final StatusMessage MSG = new StatusMessage();

    @Inject
    private StatusReporter statusReporter;
    private int counter = 0;

    @Override
    public void process(Document document, String inputViewName) {
        try {
            statusReporter.notify(MSG);
            counter++;
            SnapDataException ex = new SnapDataException(document, ERR_ERROR_BOT);
            errorViews.write(ex);
        } finally {
            statusReporter.reset();
        }
    }

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
    }

    @Override
    public void cleanup() throws ExecutionException {
        log.debug(String.format(DEBUG_ERROR_BOT, counter));
    }
}
