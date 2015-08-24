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

import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.ArchiveTypeNotImplementedDefect;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PMarker;

public class CheckCorrectArchiveType implements PMarkerCheck {

    public List<Defect> checkForErrors(PMarker pMarker) {
        List<Defect> defects = new ArrayList<Defect>();

        for (PArchive pArchive : pMarker.getPArchives()) {
            String type = pArchive.getInternalType();
            if ("jar".equalsIgnoreCase(type)) {
                continue;
            }
            if ("war".equalsIgnoreCase(type)) {
                continue;
            }
            if ("ear".equalsIgnoreCase(type)) {
                continue;
            }
            if ("zip".equalsIgnoreCase(type)) {
                continue;
            }
            if ("tar".equalsIgnoreCase(type)) {
                continue;
            }
            defects.add(new ArchiveTypeNotImplementedDefect(pMarker, pArchive));
        }
        return defects;
    }
}
