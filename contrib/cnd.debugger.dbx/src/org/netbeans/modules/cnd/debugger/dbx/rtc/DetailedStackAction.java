/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.netbeans.modules.cnd.debugger.dbx.rtc;

import java.awt.event.ActionEvent;

import org.openide.util.HelpCtx;

import org.netbeans.modules.cnd.debugger.common2.utils.FlyweightBooleanStateAction;

/**
 * Show blocks in use since last time.
 */

class DetailedStackAction extends FlyweightBooleanStateAction {

    private final static class SharedDetailedStackAction extends Shared {
	// like SystemAction
	protected String iconResource() {
	    return "org/netbeans/modules/cnd/debugger/dbx/resources/icons/rtc/detail_stack_16.png";	// NOI18N
	}

	// like SystemAction
	public HelpCtx getHelpCtx() {
	    return null;
	}

	// interface SharedClassObject
	protected void initialize() {
	    super.initialize();
	    putValue(NAME, Catalog.get("DetailedStack"));
	    putValue(SHORT_DESCRIPTION, Catalog.get("DetailedStack"));
	    setMnemonic(Catalog.getMnemonic("MNEM_DetailedStack"));
	}
    }

    private final RtcView rtcView;

    public DetailedStackAction(RtcView rtcView) {
	super(SharedDetailedStackAction.class);
	this.rtcView = rtcView;
    }

    // interface Action
    public void actionPerformed(ActionEvent e) {
	super.actionPerformed(e);
	rtcView.showStackDetails(true);
    }
}