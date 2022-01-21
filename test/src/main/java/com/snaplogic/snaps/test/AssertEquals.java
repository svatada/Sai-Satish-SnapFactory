/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2014 - 2020, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */
package com.snaplogic.snaps.test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.DependencyManager;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.common.SnapType;
import com.snaplogic.common.properties.SnapProperty;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.document.provider.ReadObjectMapper;
import com.snaplogic.document.provider.WriteObjectMapper;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.EditorContentProvider;
import com.snaplogic.snap.api.EditorProperty;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SimpleSnap;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.SnapDataException;
import com.snaplogic.snap.api.TemplateEvaluator;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.Errors;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;
import com.snaplogic.snap.api.editor.EditorPropertyFactory;
import com.snaplogic.snap.api.editor.JsonEditorContentProviderImpl;
import com.snaplogic.snap.api.editor.JsonTemplateEvaluatorImpl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.snaplogic.snaps.test.Messages.*;

/**
 * Checks if data objects from input views are as expected.
 *
 */
@General(title = ASSERT_EQUALS_LABEL, purpose = ASSERT_EQUALS_PURPOSE)
@Category(snap = SnapCategory.TRANSFORM)
@Inputs(max = 1, min = 1, accepts = {ViewType.DOCUMENT})
@Outputs(max = 1, min = 0, offers = {ViewType.DOCUMENT})
@Errors(min = 1, max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
public class AssertEquals extends SimpleSnap implements DependencyManager {
    private static final String DEFAULT_VALUE_PATH = "/assert_equals_default.txt";
    private static final String ERROR = "error";
    private static final String EXPECTED = "expected";
    private static final ReadObjectMapper MAPPER = new ReadObjectMapper();
    private static final TypeReference<List<Object>> TYPE_REF =
            new TypeReference<List<Object>>() {};
    private List<Object> expectedData;
    private int expectedSize;
    private EditorProperty editorProperty = null;
    private static final String EVALUATE_WITH_INPUT_DOCUMENT_PROP = "evaluateWithInputDocument";
    private Boolean EVALUATE_WITH_INPUT_DOCUMENT_DEFAULT_VALUE = Boolean.TRUE;
    private boolean evaluateExpressions;

    @Inject
    private EditorPropertyFactory editorPropertyFactory;
    @Inject
    private EditorContentProvider editorContentProvider;
    @Inject
    private TemplateEvaluator templateEvaluator;
    @Inject
    private WriteObjectMapper writeObjectMapper;
    @Inject
    private ReadObjectMapper readObjectMapper;
    @Override
    public Module getManagedModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(EditorContentProvider.class).to(JsonEditorContentProviderImpl.class);
                bind(TemplateEvaluator.class).to(JsonTemplateEvaluatorImpl.class);
            }
        };
    }

    /**
     * @return The default value for the editor content.
     * @throws IOException If there was a problem reading the content.
     */
    private static String readDefaultValue() throws IOException {
        return IOUtils.toString(AssertEquals.class.getResourceAsStream(DEFAULT_VALUE_PATH));
    }

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        try {
            editorPropertyFactory.createEditorProperty(propertyBuilder,
                    SnapProperty.EditorType.JSON, EDIT_JSON_LABEL, EDIT_JSON_DESC)
                    .defaultValue(readDefaultValue())
                    .required()
                    .add();
            propertyBuilder.describe(EVALUATE_WITH_INPUT_DOCUMENT_PROP,
                    EVALUATE_WITH_INPUT_DOCUMENT_LABEL, EVALUATE_WITH_INPUT_DOCUMENT_DESC)
                    .type(SnapType.BOOLEAN)
                    .defaultValue(EVALUATE_WITH_INPUT_DOCUMENT_DEFAULT_VALUE)
                    .add();
        } catch (IOException e) {
            throw new ConfigurationException(e, ERR_DEFAULT_VALUE)
                    .withReason(e.getMessage())
                    .withResolutionAsDefect();
        }
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
        editorProperty = propertyValues.getEditorProperty(
                editorContentProvider, templateEvaluator);
        evaluateExpressions =  !Boolean.FALSE.equals(propertyValues.get(
                EVALUATE_WITH_INPUT_DOCUMENT_PROP));
    }

    private void throwException(Throwable cause, String reason, String expected) {
        throw new ExecutionException(cause, ERR_JSON_PARSER).formatWith(expected)
                .withReason(reason)
                .withResolution(RESOLUTION_EXPECTED);
    }

    @Override
    public void execute() {
        if (!TestUtils.hasInputDocuments(inputViews)) {
            computeExpectedData(null);
        }
        super.execute();
    }

    @Override
    protected void process(final Document document, final String inputViewName) {

        computeExpectedData(document);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document actualDocument;
        try {
            // de/serialize the incoming document to guarantee we have the same type
            // representations.
            writeObjectMapper.writeValue(baos, document.get());
            Object actual = readObjectMapper.readValue(baos.toByteArray(), Object.class);
            actualDocument = documentUtility.newDocument(actual);
        } catch (IOException e) {
            SnapDataException dataException = new SnapDataException(e, e.getMessage())
                    .withResolution(PROVIDED_INPUT_DOCUMENT_IS_NOT_SERIALIZABLE);
            errorViews.write(dataException, document);
            return;
        }
        BigInteger actualMd5 = new BigInteger(actualDocument.getMd5());
        Object expected = expectedData.isEmpty() ? null : expectedData.remove(0);
        Document expectedDocument = documentUtility.newDocument(expected);
        BigInteger expectedMd5 = new BigInteger(expectedDocument.getMd5());
        if (expectedMd5.equals(actualMd5)) {
            outputViews.write(document);
        } else {
            Map<String, Object> errorMap = Maps.newHashMapWithExpectedSize(3);
            errorMap.put(ERROR, REASON_NOT_EQUAL);
            errorMap.put(EXPECTED, expected);
            SnapDataException ex = new SnapDataException(
                    documentUtility.newDocument(errorMap), ERR_NOT_EQUAL)
                    .formatWith(expected, document.get())
                    .withReason(REASON_NOT_EQUAL)
                    .withResolution(RESOLUTION_NOT_EQUAL);
            errorViews.write(ex, document);
        }
    }

    private List<Object> computeExpectedData(Document document) {
        String expectedEditorData = evaluateExpressions ? editorProperty.eval(document)
                : editorProperty.eval(null);
        if (expectedData == null) {
            try {
                expectedData = MAPPER.readValue(expectedEditorData, TYPE_REF);
            } catch (JsonMappingException e) {
                throwException(e, REASON_NOT_LIST +
                                StringUtils.SPACE +
                                Throwables.getRootCause(e).getMessage(),
                        expectedEditorData);
            } catch (JsonParseException e) {
                throwException(e, REASON_INVALID_JSON +
                                StringUtils.SPACE +
                                Throwables.getRootCause(e).getMessage(),
                        expectedEditorData);
            } catch (Exception e) {
                throwException(e, Throwables.getRootCause(e).getMessage(), expectedEditorData);
            }
            expectedSize = expectedData.size();
        }
        return expectedData;
    }

    @Override
    public void cleanup() throws ExecutionException {
        if (!isDataCleanedUp(expectedData)) {
            throw new ExecutionException(ERR_LESS_INPUT).formatWith(expectedSize)
                    .withReason(REASON_LESS_INPUT)
                    .withResolution(RESOLUTION_NOT_EQUAL);
        }
    }

    private boolean isDataCleanedUp(List<Object> data) {
        if (CollectionUtils.isEmpty(data)) {
            return true;
        } else if (data.size() == 1 && data.get(0) instanceof Map
            && MapUtils.isEmpty((Map) data.get(0))) {
            return true;
        }
        return false;
    }
}
