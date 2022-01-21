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

import com.google.common.collect.Sets;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import static com.snaplogic.snaps.test.Messages.DEBUG_SANK_DOCUMENT_KEY;
import static com.snaplogic.snaps.test.Messages.DEBUG_SANK_DOCUMENT_LIST_ELEM;
import static com.snaplogic.snaps.test.Messages.DEBUG_SANK_DOCUMENT_VALUE;
import static com.snaplogic.snaps.test.Messages.DEBUG_SANK_TOTAL_DOCS;
import static com.snaplogic.snaps.test.Messages.DESC_ACK_TYPE;
import static com.snaplogic.snaps.test.Messages.LBL_ACK_TYPE;
import static com.snaplogic.snaps.test.Messages.SINK_BOT_SNAP_LABEL;
import static com.snaplogic.snaps.test.Messages.SINK_BOT_SNAP_PURPOSE;
import static com.snaplogic.snaps.test.Messages.TRACE_DOCS_RECEIVED;

/**
 * A 'sink' snap will simply swallow all Documents passed in and throw them away: if tracing is
 * enabled on the log, it will emit an ongoing count of documents sank.
 *
 * @author marco@snaplogic.com (Marco Massenzio)
 */
@General(title = SINK_BOT_SNAP_LABEL, purpose = SINK_BOT_SNAP_PURPOSE)
@Category(snap = SnapCategory.WRITE)
@Inputs(min = 1, max = 1, accepts = { ViewType.DOCUMENT })
@Outputs(max = 0)
@Version(snap = 1)
public class SinkBot extends SimpleSnap {
    private static final Logger log = LoggerFactory.getLogger(SinkBot.class);
    private static final String PROP_ACK_TYPE = "ack_type";
    private int counter = 0;
    private AckType ackType;
    private final Random random = new Random(System.currentTimeMillis());

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        propertyBuilder.describe(PROP_ACK_TYPE, LBL_ACK_TYPE, DESC_ACK_TYPE)
                .withAllowedValues(Sets.newEnumSet(Arrays.asList(AckType.values()),
                        AckType.class))
                .defaultValue(AckType.ALL.name())
                .add();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Document document, String inputViewName) {
        Object obj = document.get();
        if (obj instanceof Map) {
            Iterator<Entry<String, Object>> it = document.get(Map.class).entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Object> entry = it.next();
                if (log.isTraceEnabled()) {
                    log.trace(String.format(DEBUG_SANK_DOCUMENT_KEY, entry.getKey()));
                    log.trace(String.format(DEBUG_SANK_DOCUMENT_VALUE, entry.getValue()));
                }
            }
        } else if (obj instanceof List) {
            Iterator<Object> it = ((List<Object>) document.get()).iterator();
            while (it.hasNext()) {
                Object elem = it.next();
                if (log.isTraceEnabled()) {
                    log.trace(String.format(DEBUG_SANK_DOCUMENT_LIST_ELEM, elem));
                }
            }
        }
        switch (ackType) {
            case ALL:
                document.acknowledge();
                break;
            case ALTERNATE:
                if(counter % 2 == 0) {
                    document.acknowledge();
                }
                break;
            case RANDOM:
                if (random.nextBoolean()) {
                   document.acknowledge();
                }
                break;
            case NONE:
                // Do nothing. No ACK
        }
        if (++counter % 97 == 0) {
            if (log.isTraceEnabled()) {
                log.trace(String.format(TRACE_DOCS_RECEIVED, counter));
            }
        }
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
        String ackTypeString = propertyValues.get(PROP_ACK_TYPE);
        ackType = AckType.valueOf(ackTypeString.toUpperCase());
    }

    @Override
    public void cleanup() throws ExecutionException {
        log.debug(String.format(DEBUG_SANK_TOTAL_DOCS, counter));
    }

    //------ ACK Type
    /**
     * Ack type enum
     */
    public static enum AckType {
        /**
         * All the document will be ACKed. This is the default behaviour
         */
        ALL,
        /**
         * Documents will be ACKed at random
         */
        RANDOM,
        /**
         * Every other document will be ACKed
         */
        ALTERNATE,
        /**
         * None of the document will be ACKed.
         */
        NONE;
    }
}
