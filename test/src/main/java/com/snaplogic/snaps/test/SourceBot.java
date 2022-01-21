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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ViewProvider;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.common.properties.builders.ViewBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.ViewCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

import static com.snaplogic.snaps.test.Messages.*;

/**
 * SourceBot
 *
 * <p>Generates a pre-defined number of documents, each with a defined number of fields,
 * whose value
 * can also be defined by a {@code value} property.
 *
 * <p>This initial implementation is <strong>very</strong> rough: it only emits the ordinal number
 * of the field and the same value for each field: a future implementation should allow the user to
 * define {name, value} pairs for the fields to emit in each document.
 *
 * <p>This is expected to be mainly a 'debug' snap, just to test functionality of other snaps,
 * feeding them with an easy-to-setup know stream of documents
 *
 * @author marco@snaplogic.com (Marco Massenzio)
 */
@General(title = SOURCE_BOT_SNAP_LABEL, purpose = SOURCE_BOT_SNAP_PURPOSE)
@Category(snap = SnapCategory.READ)
@Inputs(max = 0)
@Outputs(min = 1, max = Integer.MAX_VALUE, offers = { ViewType.DOCUMENT })
@Version(snap = 1)
public class SourceBot extends SimpleSnap implements ViewProvider {
    static final String COUNT_PROP = "count";
    static final String NUM_FIELDS_PROP = "num_fields";
    static final String VALUE_PROP = "value";
    static final String VALUE_TYPE_PROP = "value_type";
    private final static String DEFAULT_OUTPUT_VIEW_0 = "output0";
    private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeParser();
    private static final Set<String> allowedValues = ImmutableSet.of(
            SnapType.STRING.toString(),
            SnapType.INTEGER.toString(),
            SnapType.NUMBER.toString(),
            SnapType.DATE.getJsonFormat(),
            SnapType.DATETIME.getJsonFormat(),
            SnapType.TIME.getJsonFormat());
    private BigInteger count;
    private String valueType;
    private BigInteger numberFields;
    private String value;

    @Override
    public void defineViews(final ViewBuilder viewBuilder) {
        viewBuilder.describe(DEFAULT_OUTPUT_VIEW_0)
                .type(ViewType.DOCUMENT)
                .add(ViewCategory.OUTPUT);
    }

    @Override
    public void defineProperties(final PropertyBuilder builder) {
        builder.describe(COUNT_PROP, COUNT_LABEL, COUNT_DESC)
                .required()
                .type(SnapType.NUMBER)
                .defaultValue(10)
                .add();
        builder.describe(NUM_FIELDS_PROP, NUM_FIELDS_LABEL, NUM_FIELDS_DESC)
                .required()
                .type(SnapType.NUMBER)
                .defaultValue(5)
                .add();
        builder.describe(VALUE_PROP, VALUE_LABEL, VALUE_DESC)
                .required()
                .type(SnapType.STRING)
                .defaultValue("dummy")
                .add();
        builder.describe(VALUE_TYPE_PROP, VALUE_TYPE_LABEL, VALUE_TYPE_DESC)
                .required()
                .type(SnapType.STRING)
                .defaultValue(SnapType.STRING.toString())
                .withAllowedValues(allowedValues)
                .add();
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
        count = propertyValues.get(COUNT_PROP);
        valueType = propertyValues.get(VALUE_TYPE_PROP);
        numberFields = propertyValues.get(NUM_FIELDS_PROP);
        value = propertyValues.get(VALUE_PROP);
    }

    @Override
    public void process(Document document, String inputViewName) {
        final SnapType type;
        if (valueType == null) {
            type = SnapType.STRING;
        } else {
            type = SnapType.parse(valueType);
        }
        final Object docValue;
        Map<String, Object> doc = Maps.newLinkedHashMap();
        switch (type) {
            case DATETIME:
                docValue = dateTimeFormatter.parseDateTime(value);
                break;
            case DATE:
                docValue = dateTimeFormatter.parseLocalDate(value);
                break;
            case TIME:
                docValue = dateTimeFormatter.parseLocalTime(value);
                break;
            case NUMBER:
                docValue = new BigDecimal(value);
                break;
            case INTEGER:
                docValue = new BigInteger(value);
                break;
            case STRING:
                docValue = String.valueOf(value);
                break;
            default:
                throw new IllegalArgumentException(ERR_TYPE_NOT_SUPPORTED);
        }
        int numFiles = numberFields.intValue();
        for (int j = 0; j < numFiles; j++) {
            doc.put(Integer.toString(j), docValue);
        }
        long countVal = count.longValue();
        for (long i = 0; i < countVal; i+=1) {
            outputViews.write(documentUtility.newDocument(doc));
        }
    }
}
