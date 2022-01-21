/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2006 - 2012, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */
package com.snaplogic.snaps.test;

/**
 * @author mklumpp
 *
 */
@SuppressWarnings("nls")
class Messages {
    // Error Bot Snap
    static final String ERROR_BOT_LABEL = "Error Bot";
    static final String ERROR_BOT_PURPOSE = "Writes all documents to the error view.";
    static final String VALUE_SUGGEST_BOT_LABEL = "Value Suggest Bot";
    static final String VALUE_SUGGEST_BOT_PURPOSE = "Used for testing value suggest functionality.";
    static final String THROTTLE_BOT_LABEL = "Throttle Bot";
    static final String THROTTLE_BOT_PURPOSE = "Throttles all the incoming document at the " +
            "configured throttle speed.";
    static final String THROTTLE_BOT_SPEED_LABEL = "Speed";
    static final String THROTTLE_BOT_THROTTLE_TYPE = "Throttle type";
    static final String DEBUG_ERROR_BOT = "Error: %d documents written to error view";
    static final String ERR_ERROR_BOT = "I always fail";
    // Fail Bot Snap
    static final String FAIL_BOT_SNAP_LABEL = "Fail Bot";
    static final String FAIL_BOT_SNAP_PURPOSE = "Do-nothing fail snap.";
    static final String ERR_FAIL_BOT = "I'm a bad snap";
    static final String MISCONFIGURED_BOT = "I throw an exception during configure";
    // Sink Bot Snap
    static final String SINK_BOT_SNAP_LABEL = "Sink Bot";
    static final String SINK_BOT_SNAP_PURPOSE = "Do-nothing sink test snap.";
    static final String TRACE_DOCS_RECEIVED = "Received %d documents";
    static final String DEBUG_SANK_TOTAL_DOCS = "Sank %d Documents in total";
    static final String DEBUG_SANK_DOCUMENT_KEY = "Key: %s";
    static final String DEBUG_SANK_DOCUMENT_VALUE = "Value: %s";
    static final String DEBUG_SANK_DOCUMENT_LIST_ELEM = "Elem: %s";
    // Source Bot Snap
    static final String SOURCE_BOT_SNAP_LABEL =  "SourceBot";
    static final String SOURCE_BOT_SNAP_PURPOSE =  "Test snap that generates document data.";
    static final String COUNT_LABEL = "Count";
    static final String COUNT_DESC = "How many documents to generate";
    static final String NUM_FIELDS_LABEL = "Fields";
    static final String NUM_FIELDS_DESC = "How many fields per document";
    static final String VALUE_LABEL = "Value";
    static final String VALUE_DESC = "Value of each field in each document";
    static final String VALUE_TYPE_LABEL = "Value type";
    static final String VALUE_TYPE_DESC = "Value type of each field in each document";
    static final String ERR_TYPE_NOT_SUPPORTED = "Value type %s is not supported";
    static final String DESC_SPEED = "Speed at which documents should be throttled (in " +
            "milli seconds). In case of Random throttling type, " +
            "the value of this property is used as the maximum random value that will be " +
            "generated. In case of Exponential throttling type, this value is used as the " +
            "constant that is multiplied with the calculated exponential value, " +
            "a exp(b) where a is the value of this property and b is the " +
            "document index. In case of logarithmic throttling type, this value is used as the " +
            "constant that is multiplied with the calculated log value i.e., " +
            "a log(b) where a is the value of this property and b is the document index";
    static final String SNAP_INTERRUPTED_WHILE_WAITING = "Snap interrupted while waiting to send" +
             " the document";
    static final String INTERRUPTED_DURING_DOCUMENT_THROTTLING = "Interrupted during document " +
             "throttling";
    static final String DESC_TYPE_OF_THROTTLING = "Type of throttling - Random, Fixed, " +
            "Exponential or Logarithmic";
    static final String LBL_ACK_TYPE = "ACK Type";
    static final String DESC_ACK_TYPE = "Acknowledgement type can be one of the following options" +
            " - All (All document will be ACKed), " +
            "Random (Random document will be ACKed), " +
            "Alternate (ACK will be sent to every other document), " +
            "None (None of the document will be ACKed).";
    // Assert Equals Snap
    static final String ASSERT_EQUALS_LABEL = "Assert Equals";
    static final String ASSERT_EQUALS_PURPOSE = "Checks if input documents are as expected.";
    static final String EDIT_JSON_LABEL = "Edit expected JSON data";
    static final String EDIT_JSON_DESC = "Please edit expected JSON data.";
    static final String EVALUATE_WITH_INPUT_DOCUMENT_LABEL = "Evaluate with Input Document";
    static final String EVALUATE_WITH_INPUT_DOCUMENT_DESC = "Evaluates the data present" +
            " in the Editor with data from the Input Document.";
    static final String ERR_JSON_PARSER = "Failed to parse expected JSON data: %s";
    static final String REASON_NOT_LIST = "Expected JSON data is not a List object.";
    static final String REASON_INVALID_JSON = "Invalid JSON format.";
    static final String RESOLUTION_EXPECTED = "Please check expected JSON data";
    static final String ERR_DEFAULT_VALUE = "Unable to read default value";
    static final String ERR_NOT_EQUAL = "Input data object is not as expected, Expected: %s," +
            " Actual: %s";
    static final String REASON_NOT_EQUAL = "Expected and input data are not equal";
    static final String RESOLUTION_NOT_EQUAL = "Please check expected and input data.";
    static final String PROVIDED_INPUT_DOCUMENT_IS_NOT_SERIALIZABLE = "Provided input document is" +
            " not serializable";
    static final String ERR_LESS_INPUT = "Not enough input documents, expected size: %d";
    static final String REASON_LESS_INPUT = "The number of input documents is smaller than" +
            " expected";
    // System state recorder
    static final String SYS_STATE_LABEL = "System State";
    static final String SYS_STATE_PURPOSE = "Publishes the system state information";
}
