/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2013, SnapLogic, Inc.  All rights reserved.
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
import com.snaplogic.common.SnapType;
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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.snaplogic.snaps.test.Messages.*;

/**
 * Throttle bot will throttle the incoming documents at the given rate.
 *
 * @author ksubramanian
 */
@General(title = THROTTLE_BOT_LABEL, purpose = THROTTLE_BOT_PURPOSE)
@Category(snap = SnapCategory.WRITE)
@Inputs(min = 1, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
public class ThrottleBot extends SimpleSnap {
    private static final String PROP_THROTTLE_TYPE = "throttle_type";
    private static final String PROP_SPEED = "speed";
    private static final int MAX_WAIT_TIME = 5000;
    private final Set<String> types = Sets.newHashSet(
            ThrottleType.RANDOM.toString(),
            ThrottleType.FIXED.toString(),
            ThrottleType.EXPONENTIAL.toString(),
            ThrottleType.LOGARITHMIC.toString());
    private ThrottlePolicy throttlePolicy;

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        propertyBuilder.describe(PROP_THROTTLE_TYPE, THROTTLE_BOT_THROTTLE_TYPE,
                DESC_TYPE_OF_THROTTLING)
                .withAllowedValues(types)
                .defaultValue(ThrottleType.FIXED.toString()).add();
        propertyBuilder.describe(PROP_SPEED, THROTTLE_BOT_SPEED_LABEL, DESC_SPEED)
                .type(SnapType.INTEGER)
                .withMinValue(0).add();
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
        ThrottleType type = ThrottleType.getTypeFor(propertyValues.get(PROP_THROTTLE_TYPE));
        BigInteger speed = propertyValues.get(PROP_SPEED);
        throttlePolicy = new ThrottlePolicy(type, speed == null ? 0 : speed.intValue());
    }

    @Override
    public void process(Document document, String inputViewName) {
        throttlePolicy.throttle();
        outputViews.write(document);
    }

    @Override
    public void cleanup() throws ExecutionException {
    }

    //---------------- Private class and enums ----------------------------------------
    private class ThrottlePolicy {
        private final Random random = new Random(System.currentTimeMillis());
        private final ThrottleType throttleType;
        private final int throttleSpeed;
        private int counter = 0;

        private ThrottlePolicy(final ThrottleType throttleType, final int throttleSpeed) {
            this.throttleType = throttleType;
            if (throttleType == ThrottleType.RANDOM && throttleSpeed <= 0) {
                this.throttleSpeed = MAX_WAIT_TIME;
            } else {
                this.throttleSpeed = throttleSpeed;
            }
        }

        public void throttle() {
            int waitTimeInMs = calculateThrottleTime();
            try {
                Thread.sleep(waitTimeInMs);
            } catch (InterruptedException e) {
                throw new ExecutionException(e, INTERRUPTED_DURING_DOCUMENT_THROTTLING)
                        .withReason(SNAP_INTERRUPTED_WHILE_WAITING);
            }
        }

        private int calculateThrottleTime() {
            int waitTimeInMs;
            switch (throttleType) {
                case RANDOM:
                    waitTimeInMs = Math.abs(random.nextInt(throttleSpeed));
                    break;
                case EXPONENTIAL:
                    waitTimeInMs = throttleSpeed * (int) Math.round(Math.exp(++counter));
                    break;
                case LOGARITHMIC:
                    waitTimeInMs = throttleSpeed * (int) Math.round(Math.log(++counter));
                    break;
                case FIXED:
                default:
                    // Fixed throttling
                    waitTimeInMs = throttleSpeed;
            }
            return waitTimeInMs;
        }
    }
    private enum ThrottleType {
        RANDOM("Random"),
        FIXED("Fixed"),
        EXPONENTIAL("Exponential"),
        LOGARITHMIC("Logarithmic");

        private final String name;
        private static final Map<String, ThrottleType> typeMap = new HashMap<String,
                ThrottleType>() {
                    {
                        put(RANDOM.toString(), RANDOM);
                        put(FIXED.toString(), FIXED);
                        put(EXPONENTIAL.toString(), EXPONENTIAL);
                        put(LOGARITHMIC.toString(), LOGARITHMIC);
                    }
        };

        /**
         * Returns the throttle type enum for the given name string.
         *
         * @param name
         * @return throttleType
         */
        public static final ThrottleType getTypeFor(String name) {
            return typeMap.get(name);
        }

        private ThrottleType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
