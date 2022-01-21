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

import com.google.inject.Inject;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.api.Snap;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.Suggestions;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.common.properties.builders.SuggestionBuilder;
import com.snaplogic.snap.api.DocumentUtility;
import com.snaplogic.snap.api.OutputViews;
import com.snaplogic.snap.api.PropertyCategory;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.Errors;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.snaplogic.snaps.test.Messages.VALUE_SUGGEST_BOT_LABEL;
import static com.snaplogic.snaps.test.Messages.VALUE_SUGGEST_BOT_PURPOSE;

/**
 * ValueSuggestBot is the snap that is used for testing value suggest functionality.
 * This snap has one output and writes one document that contains the suggested value.
 *
 * @author ksubramanian
 * @since 2013
 */
@Version(snap = 1)
@General(title = VALUE_SUGGEST_BOT_LABEL, purpose = VALUE_SUGGEST_BOT_PURPOSE)
@Inputs(min = 0, max = 0)
@Outputs(min = 1, max = 1)
@Errors(min = 1, max =  1, offers = {ViewType.DOCUMENT})
@Category(snap = SnapCategory.READ)
public class ValueSuggestBot implements Snap {

    private static final String PROP_NAME = "name";
    private static final String PROP_ECHO = "echo";
    private static final String PROP_PARENT = "parent";
    private static final String PROP_COMP_PARENT = "composite_parent";
    private static final String PROP_CHILD = "child";
    private static final String PROP_COMP_CHILD = "composite_child";
    private String valueToWrite;

    @Inject
    private DocumentUtility documentUtility;
    @Inject
    private OutputViews outputViews;

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        propertyBuilder.describe(PROP_NAME, PROP_NAME)
                .expression()
                .add();
        propertyBuilder.describe(PROP_ECHO, PROP_ECHO)
                .withSuggestions(new Suggestions() {
                    @Override
                    public void suggest(SuggestionBuilder suggestionBuilder,
                            PropertyValues propertyValues) {
                        String name = propertyValues.get(PropertyCategory.SETTINGS, PROP_NAME);
                        suggestionBuilder.node(PROP_ECHO).suggestions(name);
                    }
                }).add();

        final Set<String> valueSet = new HashSet<>();
        valueSet.add("suggest_value");
        SnapProperty sp = propertyBuilder.describe(PROP_CHILD, PROP_COMP_CHILD, PROP_COMP_CHILD)
                .withSuggestions(new Suggestions() {
                    @Override
                    public void suggest(SuggestionBuilder suggestionBuilder,
                                        PropertyValues propertyValues) {
                        suggestionBuilder.node(PROP_PARENT).over("value").over(PROP_CHILD)
                                .suggestions(valueSet.toArray(new String[0]));
                    }
                })
                .build();
        propertyBuilder.describe(PROP_PARENT, PROP_COMP_PARENT, PROP_COMP_PARENT)
                .type(SnapType.COMPOSITE)
                .withEntry(sp)
                .add();


        SnapProperty sp2 = propertyBuilder.describe(PROP_CHILD, PROP_COMP_CHILD, PROP_COMP_CHILD)
                .withSuggestions(new Suggestions() {
                    @Override
                    public void suggest(SuggestionBuilder suggestionBuilder,
                                        PropertyValues propertyValues) {
                        suggestionBuilder.node("prop4").over("value").over("prop22")
                                .over("value").over("prop21").over(PROP_CHILD)
                                .suggestions(valueSet.toArray(new String[0]));
                    }
                })
                .build();
        SnapProperty sp21 = propertyBuilder.describe("prop21", "Table1", PROP_COMP_CHILD)
                .type(SnapType.TABLE)
                .withEntry(sp2)
                .build();
        SnapProperty sp22 = propertyBuilder.describe("prop22", "Composite1", PROP_COMP_CHILD)
                .type(SnapType.COMPOSITE)
                .withEntry(sp21)
                .build();

        SnapProperty sp3 = propertyBuilder.describe(PROP_CHILD, PROP_COMP_CHILD, PROP_COMP_CHILD)
                .withSuggestions(new Suggestions() {
                    @Override
                    public void suggest(SuggestionBuilder suggestionBuilder,
                                        PropertyValues propertyValues) {
                        suggestionBuilder.node("prop4").over("value").over("prop32")
                                .over("value").over("prop31").over(PROP_CHILD)
                                .suggestions(valueSet.toArray(new String[0]));
                    }
                })
                .build();
        SnapProperty sp31 = propertyBuilder.describe("prop31", "Table2", PROP_COMP_CHILD)
                .type(SnapType.TABLE)
                .withEntry(sp3)
                .build();
        SnapProperty sp32 = propertyBuilder.describe("prop32", "Composite2", PROP_COMP_CHILD)
                .type(SnapType.COMPOSITE)
                .withEntry(sp31)
                .build();

        List<SnapProperty> compositeProperties = new ArrayList<>();
        compositeProperties.add(sp22);
        compositeProperties.add(sp32);
        propertyBuilder.describe("prop4", "Composite3", PROP_COMP_PARENT)
                .type(SnapType.COMPOSITE)
                .withEntries(compositeProperties)
                .add();
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
        valueToWrite = propertyValues.get(PROP_ECHO);
    }

    @Override
    public void execute() throws ExecutionException {
        outputViews.write(documentUtility.newDocument(valueToWrite));
    }

    @Override
    public void cleanup() throws ExecutionException {
        // NOOP
    }
}
