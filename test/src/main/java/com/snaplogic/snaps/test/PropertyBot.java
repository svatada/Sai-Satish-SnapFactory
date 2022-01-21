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

import com.snaplogic.api.Suggester;
import com.snaplogic.common.jsonpath.JsonPath;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.jsonpath.JsonPaths;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.TypedExpressionProperty;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.DisplayHints;
import com.snaplogic.snap.api.capabilities.Errors;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.PlatformFeature;
import com.snaplogic.snap.api.capabilities.Property;
import com.snaplogic.snap.api.capabilities.Suggestable;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * @author tstack
 */
@General(title = "Property Bot", purpose = "A snap that has many different types of properties")
@Category(snap = SnapCategory.TRANSFORM)
@Inputs(min = 1, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Errors(offers = {ViewType.DOCUMENT})
@Version(snap = 1)
@PlatformFeature(coerceAndValidateExpressions = true)
public class PropertyBot extends SimpleSnap {

    @Property(label = "String", description = "String")
    private String string = "abc";

    @Property(label = "String with Pattern", description = "String with Pattern")
    @Pattern(regexp = "^[a-z].*")
    private String stringWithPattern = "abc123";

    @Property(label = "Number", description = "Number")
    private Number number = 0;

    @Property(label = "Number with Minimum", description = "Number with Minimum")
    @Min(10)
    private Number numberWithMinimum = 100;

    @Property(label = "Number with Maximum", description = "Number with Maximum")
    @Max(1000)
    private Number numberWithMaximum = 10;

    @Property(label = "Number Expression with Min/Max", description = "Number")
    @Min(100)
    @Max(2000)
    private TypedExpressionProperty<Number> numberExpr = TypedExpressionProperty.constant(200);

    @Property(label = "JSON-Path", description = "JSON-Path")
    private JsonPath jsonPath = JsonPaths.root();

    @Property(label = "Pipeline", description = "Pipeline")
    @DisplayHints(type = SnapProperty.DisplayType.PIPELINE_PATH_BROWSING)
    private TypedExpressionProperty<Optional<String>> pipelinePath;

    @Property(label = "Snaplex", description = "Snaplex")
    @DisplayHints(type = SnapProperty.DisplayType.SNAPLEX_PATH_BROWSING)
    private TypedExpressionProperty<Optional<String>> snaplexPath;

    @Property(label = "File", description = "File")
    @DisplayHints(type = SnapProperty.DisplayType.FILE_BROWSING)
    private TypedExpressionProperty<Optional<String>> fileName;

    @Property(label = "Driver", description = "Driver")
    @DisplayHints(type = SnapProperty.DisplayType.DRIVER_BROWSING)
    private Optional<String> driver;

    @Property(label = "Table", description = "Table")
    @DisplayHints(type = SnapProperty.DisplayType.TABLE_PATH_BROWSING)
    private Optional<String> table;

    public static class DemoSuggester implements Suggester<String> {
        @Override
        public List<String> execute() {
            return Arrays.asList(
                    "Hello, World!",
                    "Test"
            );
        }
    }

    @Property(label = "Suggestable", description = "Suggestable")
    @DisplayHints(type = SnapProperty.DisplayType.SUGGESTABLE_NOEXPRESSION)
    @Suggestable(DemoSuggester.class)
    private String suggestable = "Test";

    @Override
    protected void process(final Document document, final String inputViewName) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("string", string);
        map.put("stringWithPattern", stringWithPattern);
        map.put("number", number);
        map.put("numberWithMinimum", numberWithMinimum);
        map.put("numberWithMaximum", numberWithMaximum);
        map.put("numberExpr", numberExpr.eval(document));
        map.put("jsonPath", jsonPath.toString());
        map.put("pipelinePath", pipelinePath.eval(document).orElse(StringUtils.EMPTY));
        map.put("snaplexPath", snaplexPath.eval(document).orElse(StringUtils.EMPTY));
        map.put("fileName", fileName.eval(document).orElse(StringUtils.EMPTY));
        map.put("driver", driver.orElse(StringUtils.EMPTY));
        map.put("table", table.orElse(StringUtils.EMPTY));
        map.put("suggestable", suggestable);

        Document newdoc = documentUtility.newDocument(map);

        outputViews.write(newdoc, document);
    }
}
