package com.geewhiz.pacify.checks.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.LinkedHashSet;

import com.geewhiz.pacify.checks.Check;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.PropertyResolveManager;

public class CheckPropertyDuplicateInPropertyFile implements Check {

	PropertyResolveManager propertyResolveManager;

	public CheckPropertyDuplicateInPropertyFile(PropertyResolveManager propertyResolveManager) {
		this.propertyResolveManager = propertyResolveManager;
	}

	public LinkedHashSet<Defect> checkForErrors() {
		LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

		defects.addAll(propertyResolveManager.checkForDuplicateEntry());

		return defects;
	}
}
