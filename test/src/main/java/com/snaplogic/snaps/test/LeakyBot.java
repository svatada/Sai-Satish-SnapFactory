/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2017 - 2020, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */

package com.snaplogic.snaps.test;

import com.snaplogic.account.api.capabilities.Accounts;
import com.snaplogic.api.LifecycleEvent;
import com.snaplogic.common.properties.builders.PropertyBuilder;
import com.snaplogic.snap.api.Document;
import com.snaplogic.snap.api.DocumentUtility;
import com.snaplogic.snap.api.ErrorViews;
import com.snaplogic.snap.api.OutputViews;
import com.snaplogic.snap.api.PropertyValues;
import com.snaplogic.snap.api.SnapCategory;
import com.snaplogic.snap.api.capabilities.Category;
import com.snaplogic.snap.api.capabilities.General;
import com.snaplogic.snap.api.capabilities.Inputs;
import com.snaplogic.snap.api.capabilities.Outputs;
import com.snaplogic.snap.api.capabilities.Version;
import com.snaplogic.snap.api.capabilities.ViewType;
import com.snaplogic.snap.api.sql.ConverterRegistryImpl;
import com.snaplogic.snap.api.sql.DatabaseAccount;
import com.snaplogic.snap.api.sql.DatabaseUtils;
import com.snaplogic.snap.api.sql.DefaultTypeMappingHandler;
import com.snaplogic.snap.api.sql.MetadataProvider;
import com.snaplogic.snap.api.sql.TableMetaData;
import com.snaplogic.snap.api.sql.TypeMappingHandler;
import com.snaplogic.snap.api.sql.accounts.LeakyAccount;
import com.snaplogic.snap.api.sql.converters.CustomTypeConverter;
import com.snaplogic.snap.api.sql.converters.registry.MySqlConverterRegistry;
import com.snaplogic.snap.api.sql.mappers.MySqlRecordMapper;
import com.snaplogic.snap.api.sql.metadata.MySqlMetadataProvider;
import com.snaplogic.snap.api.sql.operations.JdbcOperationsImpl;
import com.snaplogic.snap.api.sql.query.MySqlSuggestQueryProvider;
import com.snaplogic.snap.api.sql.query.SuggestQueryProvider;
import com.snaplogic.snap.api.sql.quotation.MySqlQuotationHandler;
import com.snaplogic.snap.api.sql.quotation.QuotationHandler;
import com.snaplogic.snaps.sql.SimpleSqlSnap;

import org.jooq.Record;
import org.jooq.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * A snap that leaks sql database connections.
 *
 * @author jbackes
 */
@Version(snap = 2)
@General(title = "Leaky Bot", purpose = "Leak connections based on input")
@Category(snap = SnapCategory.WRITE)
@Inputs(max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(max = 1)
@Accounts(provides = {LeakyAccount.class})
public class LeakyBot extends SimpleSqlSnap {
    private static final Logger LOG = LoggerFactory.getLogger(LeakyBot.class);

    private ConverterRegistryImpl converterRegistry = new MySqlConverterRegistry();
    private RecordMapper recordMapper = new MySqlRecordMapper((MySqlConverterRegistry) converterRegistry);
    private DatabaseUtils databaseUtils = new DatabaseUtils();
    private MySqlSuggestQueryProvider queryProvider = new MySqlSuggestQueryProvider();
    private QuotationHandler quotationHandler = new MySqlQuotationHandler();
    private MySqlMetadataProvider metadataProvider = new MySqlMetadataProvider(quotationHandler);
    private DefaultTypeMappingHandler typeMappingHandler = new DefaultTypeMappingHandler();
    private CustomTypeConverter customTypeConverter = new CustomTypeConverter();

    private int documentCount = 0;

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
        super.defineProperties(propertyBuilder);
    }

    @Override
    public void configure(final PropertyValues propertyValues) {
        jdbcOperations = new LeakyOperationsImpl(recordMapper,
            converterRegistry, databaseUtils, queryProvider,
            metadataProvider, typeMappingHandler,
            customTypeConverter, quotationHandler);
        documentCount = 0;
    }

    @Override
    protected String getName() {
        return "LeakyBot";
    }

    @Override
    protected void processDocument(Document document, String inputViewName) {
        // For every document received, leak one connection
        Connection conn = jdbcOperations.acquireConnection(account);
        document.acknowledge();
        switch (documentCount++) {
            case 2:
                try {
                    conn.createStatement();
                } catch (SQLException e) {
                    LOG.debug("(+) Should have closed a connection on 2nd document");
                }
                break;
            case 5:
                throw new RuntimeException("(+) Leaking w/Runtime Exception " +
                    "documentCount = " + documentCount);
            default:
                break;
        }
    }

    @Override
    public void execute() {
        super.execute();
        // Ignore
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (jdbcOperations != null) {
            jdbcOperations.cleanup();
        }
    }

    @Override
    public void handle(final LifecycleEvent event) {
        LOG.debug("(+) Handle event {}", event);
        super.handle(event);
    }

    class LeakyOperationsImpl extends JdbcOperationsImpl {

        protected LeakyOperationsImpl(RecordMapper<Record, Map<String, Object>> recordMapper,
                                      ConverterRegistryImpl converterRegistry,
                                      DatabaseUtils databaseUtils,
                                      SuggestQueryProvider suggestQueryProvider,
                                      MetadataProvider metadataProvider,
                                      TypeMappingHandler typeMappingHandler,
                                      CustomTypeConverter customTypeConverter,
                                      QuotationHandler quotationHandler) {
            super(recordMapper, converterRegistry, databaseUtils, suggestQueryProvider,
                metadataProvider, typeMappingHandler, customTypeConverter, quotationHandler);
        }

        @Override
        public int[] merge(String tableName, String idColumn, String onCondition, Map<String, Object> onConditionValueMap, Document document, DatabaseAccount account, TableMetaData tableMetaData, OutputViews outputViews, ErrorViews errorViews, DocumentUtility documentUtility, List<Document> inputDocuments, int maxRetries, long retryInterval) throws BatchUpdateException {
            return new int[0];
        }

        @Override
        public String getName() {
            return "LeakyOperationsImpl";
        }
    }
}
