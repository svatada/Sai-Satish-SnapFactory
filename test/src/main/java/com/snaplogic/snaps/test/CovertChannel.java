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

import com.google.inject.Singleton;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The covert channel used by the CovertLeftBot and CovertRightBot snaps.
 *
 * @author tstack
 */
@Singleton
public class CovertChannel {
    public final ConcurrentLinkedQueue docs = new ConcurrentLinkedQueue();
}
