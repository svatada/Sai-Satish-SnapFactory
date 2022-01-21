/*
 * SnapLogic - Data Integration
 *
 * Copyright (C) 2015, SnapLogic, Inc.  All rights reserved.
 *
 * This program is licensed under the terms of
 * the SnapLogic Commercial Subscription agreement.
 *
 * "SnapLogic" is a trademark of SnapLogic, Inc.
 */

package com.snaplogic.snaps.test;

import com.google.common.annotations.VisibleForTesting;
import com.snaplogic.api.ConfigurationException;
import com.snaplogic.api.ExecutionException;
import com.snaplogic.api.ViewProvider;
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
import com.sun.management.OperatingSystemMXBean;
import com.sun.management.UnixOperatingSystemMXBean;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import static com.snaplogic.snaps.test.Messages.SYS_STATE_LABEL;
import static com.snaplogic.snaps.test.Messages.SYS_STATE_PURPOSE;

/**
 * Writes out the system state for each incoming document. Input documents is used to trigger a
 * system state recording.
 *
 * @author ksubramanian
 */
@General(title = SYS_STATE_LABEL, purpose = SYS_STATE_PURPOSE)
@Category(snap = SnapCategory.WRITE)
@Inputs(min = 0, max = 1, accepts = {ViewType.DOCUMENT})
@Outputs(max = 1, offers = {ViewType.DOCUMENT})
@Version(snap = 1)
public class SystemState extends SimpleSnap implements ViewProvider {

    @VisibleForTesting
    static final String INCOMING_DATA = "incoming_data";
    static final String KEY_FD = "file_descriptors";
    static final String KEY_USED_MEMORY_RATIO = "used_memory_ratio";
    static final String KEY_THREAD_STATS = "thread_stats";
    static final String KEY_CPU = "cpu_time";
    static final String KEY_ALLOC = "allocated_bytes";
    static final String KEY_BUFFER_POOLS = "buffer_pools";
    static final String KEY_COMPILATION = "compilation";
    static final String KEY_CLASS_LOADING = "class_loading";
    static final String KEY_LOADED_CLASS_COUNT = "current_loaded_class_count";
    static final String KEY_TOTAL_LOADED_CLASS_COUNT = "total_loaded_class_count";
    static final String KEY_UNLOADED_CLASS_COUNT = "unloaded_class_count";
    static final String KEY_GC_STATS = "gc_stats";
    static final String KEY_COLLECTION_COUNT = "collection_count";
    static final String KEY_COLLECTION_TIME = "collection_time";
    static final String KEY_MEM_POOL_NAME = "memory_pool_names";
    static final String KEY_SYSTEM_PROPERTIES = "system_properties";
    static final String NEGATIVE_ONE = "-1.0";
    static final String CURRENT_TIME = "current_time_in_ns";
    static final String THREAD_NAME = "thread_name";
    static final String BLOCKED_COUNT = "blocked_count";
    static final String BLOCKED_TIME = "blocked_time";
    static final String THREAD_STATE = "thread_state";
    static final String WAIT_COUNT = "wait_count";
    static final String WAIT_TIME = "wait_time";
    static final String LOCK_NAME = "lock_name";
    static final String LOCK_OWNER_ID = "lock_owner_id";
    static final String LOCK_OWNER_NAME = "lock_owner_name";
    static final String THREAD_INFO = "thread_info";
    static final String DEFAULT_INPUT_VIEW = "input0";
    static final String DEFAULT_OUTPUT_VIEW = "output0";

    private OperatingSystemMXBean operatingSystemMXBean;
    private MemoryMXBean memoryMXBean;
    private ThreadMXBean threadMXBean;
    private UnixOperatingSystemMXBean unixOperatingSystemMXBean;
    private List<BufferPoolMXBean> bufferPools;
    private com.sun.management.ThreadMXBean extendedThreadBean;
    private CompilationMXBean compilationMXBean;
    private ClassLoadingMXBean classLoadingMXBean;
    private List<GarbageCollectorMXBean> garbageCollectorMXBeans;

    @Override
    public void defineProperties(final PropertyBuilder propertyBuilder) {
    }

    @Override
    public void defineViews(ViewBuilder viewBuilder) {
        viewBuilder.describe(DEFAULT_INPUT_VIEW)
                .type(ViewType.DOCUMENT)
                .add(ViewCategory.INPUT);
        viewBuilder.describe(DEFAULT_OUTPUT_VIEW)
                .type(ViewType.DOCUMENT)
                .add(ViewCategory.OUTPUT);
    }

    @Override
    public void configure(final PropertyValues propertyValues) throws ConfigurationException {
        operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        if (operatingSystemMXBean instanceof UnixOperatingSystemMXBean) {
            unixOperatingSystemMXBean = (UnixOperatingSystemMXBean) operatingSystemMXBean;
        } else {
            unixOperatingSystemMXBean = null;
        }
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        threadMXBean = ManagementFactory.getThreadMXBean();
        if (threadMXBean.isThreadCpuTimeSupported()) {
            threadMXBean.setThreadCpuTimeEnabled(true);
        }
        if (threadMXBean instanceof com.sun.management.ThreadMXBean) {
            extendedThreadBean = (com.sun.management.ThreadMXBean) threadMXBean;
            if (extendedThreadBean.isThreadAllocatedMemorySupported()) {
                extendedThreadBean.setThreadAllocatedMemoryEnabled(true);
            }
        } else {
            extendedThreadBean = null;
        }
        bufferPools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        compilationMXBean = ManagementFactory.getCompilationMXBean();
        classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    }

    @Override
    public void process(Document document, String inputViewName) {
        outputViews.write(addSystemStateTo(document));
    }

    @Override
    public void cleanup() throws ExecutionException {
    }

    //------------------------------------- Private Methods -------------------------------------//


    private Document addSystemStateTo(Document document) {
        Map<String, Object> dataMap;
        if (document == null) {
            // There is no input document, so create an output document.
            dataMap = new LinkedHashMap<>(9);
            document = documentUtility.newDocument(dataMap);
        } else {
            try {
                dataMap = document.get(Map.class);
            } catch (ClassCastException e) {
                // Data is not in map format. Lets create a new map and add it to the document.
                dataMap = new LinkedHashMap<>(10);
                dataMap.put(INCOMING_DATA, document.get());
                document = documentUtility.newDocumentFor(document, dataMap);
            }
        }
        copySystemState(dataMap);
        return document;
    }

    private void copySystemState(final Map<String, Object> dataMap) {
        dataMap.put(KEY_FD, getOpenFD());
        dataMap.put(KEY_USED_MEMORY_RATIO, getUsedMemoryRatio());
        dataMap.put(KEY_THREAD_STATS, getThreadStats());
        dataMap.put(KEY_BUFFER_POOLS, getBufferPools());
        dataMap.put(KEY_COMPILATION, compilationMXBean.getTotalCompilationTime());
        dataMap.put(KEY_CLASS_LOADING, getClassLoadingStats());
        dataMap.put(KEY_GC_STATS, getGarbageCollectorStats());
        dataMap.put(KEY_SYSTEM_PROPERTIES, getSystemProperties());
        dataMap.put(CURRENT_TIME, System.nanoTime());
    }

    private long getOpenFD() {
        if (unixOperatingSystemMXBean != null) {
            return unixOperatingSystemMXBean.getOpenFileDescriptorCount();
        } else {
            // In case of windows, there is no file descriptor limitation.
            return Long.MAX_VALUE;
        }
    }

    private BigDecimal getUsedMemoryRatio() {
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        BigDecimal totalMemory = BigDecimal.valueOf(heapMemoryUsage.getMax());
        BigDecimal usedMemory = BigDecimal.valueOf(heapMemoryUsage.getUsed());
        return usedMemory.divide(totalMemory, MathContext.DECIMAL32);
    }

    private Object getThreadStats() {
        long[] threadIds = threadMXBean.getAllThreadIds();
        Map<String, Object> threadStats = new LinkedHashMap<>();
        for (long threadId : threadIds) {
            Map<String, Object> threadStat = new LinkedHashMap<>(2);
            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
            threadStat.put(KEY_CPU, threadMXBean.getThreadCpuTime(threadId));
            if (extendedThreadBean != null) {
                threadStat.put(KEY_ALLOC, extendedThreadBean.getThreadAllocatedBytes(threadId));
            }
            threadStat.put(THREAD_INFO, getInfoFrom(threadInfo));
            threadStats.put(String.valueOf(threadId), threadStat);
        }

        return threadStats;
    }

    private Map<String, Object> getInfoFrom(final ThreadInfo threadInfo) {
        Map<String, Object> infoMap = new LinkedHashMap<>(9);
        infoMap.put(THREAD_NAME, threadInfo.getThreadName());
        infoMap.put(BLOCKED_COUNT, threadInfo.getBlockedCount());
        infoMap.put(BLOCKED_TIME, threadInfo.getBlockedTime());
        infoMap.put(THREAD_STATE, threadInfo.getThreadState().toString());
        infoMap.put(WAIT_COUNT, threadInfo.getWaitedCount());
        infoMap.put(WAIT_TIME, threadInfo.getWaitedTime());
        String lockName = threadInfo.getLockName();
        if (lockName != null) {
            infoMap.put(LOCK_NAME, lockName);
            infoMap.put(LOCK_OWNER_ID, threadInfo.getLockOwnerId());
            infoMap.put(LOCK_OWNER_NAME, threadInfo.getLockOwnerName());
        }
        return infoMap;
    }

    private Object getBufferPools() {
        Map<String, Object> bufferPoolMap = new LinkedHashMap<>();
        for (BufferPoolMXBean bufferPoolMXBean : bufferPools) {
            long totalCapacity = bufferPoolMXBean.getTotalCapacity();
            if (totalCapacity > 0) {
                long used = bufferPoolMXBean.getMemoryUsed();
                double percentageUsed = used / totalCapacity;
                bufferPoolMap.put(bufferPoolMXBean.getName(), percentageUsed);
            } else {
                // Unknown capacity.
                bufferPoolMap.put(bufferPoolMXBean.getName(), Double.valueOf(NEGATIVE_ONE));
            }
        }
        return bufferPoolMap;
    }

    private Map<String, Long> getClassLoadingStats() {
        Map<String, Long> stats = new LinkedHashMap<>(3);
        stats.put(KEY_LOADED_CLASS_COUNT, Long.valueOf(classLoadingMXBean.getLoadedClassCount()));
        stats.put(KEY_TOTAL_LOADED_CLASS_COUNT, classLoadingMXBean.getTotalLoadedClassCount());
        stats.put(KEY_UNLOADED_CLASS_COUNT, classLoadingMXBean.getUnloadedClassCount());
        return stats;
    }

    private Object getGarbageCollectorStats() {
        Map<String, Object> gcStats = new LinkedHashMap<>(garbageCollectorMXBeans.size());
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            Map<String, Object> stats = new LinkedHashMap<>(3);
            stats.put(KEY_COLLECTION_COUNT, garbageCollectorMXBean.getCollectionCount());
            stats.put(KEY_COLLECTION_TIME, garbageCollectorMXBean.getCollectionTime());
            stats.put(KEY_MEM_POOL_NAME, new ArrayList<>(Arrays.asList(garbageCollectorMXBean
                    .getMemoryPoolNames())));
            gcStats.put(garbageCollectorMXBean.getName(), stats);
        }
        return gcStats;
    }

    private Map<String, Object> getSystemProperties() {
        Properties properties = System.getProperties();
        Map<String, Object> propsMap = new LinkedHashMap<>(properties.size());
        // wrap with a TreeSet to sort the names
        for (String name : new TreeSet<>(properties.stringPropertyNames())) {
            propsMap.put(name, properties.getProperty(name));
        }
        return propsMap;
    }
}
