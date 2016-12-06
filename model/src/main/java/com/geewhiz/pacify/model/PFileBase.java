package com.geewhiz.pacify.model;

import java.io.File;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.compress.compressors.FileNameUtil;
import org.jvnet.jaxb2_commons.lang.CopyStrategy;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import com.geewhiz.pacify.defect.DefectRuntimeException;

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

public abstract class PFileBase {

    private PMarker  pMarker;
    private PArchive pArchive;

    /**
     * The physical representation. If this entry is within an archive it will be available here.
     */
    private File     file;

    public PMarker getPMarker() {
        if (getPArchive() != null)
            return pArchive.getPMarker();
        return pMarker;
    }

    public PArchive getPArchive() {
        return pArchive;
    }

    public void setPMarker(PMarker pMarker) {
        this.pMarker = pMarker;
    }

    public void setPArchive(PArchive pArchive) {
        this.pArchive = pArchive;
    }

    public Boolean isArchiveFile() {
        return getPArchive() != null;
    }

    public Boolean fileExists() {
        return file != null && file.exists();
    }

    public String getPUri() {
        StringBuffer sb = new StringBuffer();
        if (getPArchive() != null) {
            sb.append(getPArchive().getPUri());
            sb.append("!");
        }
        sb.append(getRelativePath());
        return sb.toString();
    }

    public String getBeginToken() {
        if (getInternalBeginToken() != null)
            return getInternalBeginToken();
        if (getPArchive() != null)
            return getPArchive().getBeginToken();
        return getPMarker().getBeginToken();
    }

    public String getEndToken() {
        if (getInternalEndToken() != null)
            return getInternalEndToken();
        if (getPArchive() != null)
            return getPArchive().getEndToken();
        return getPMarker().getEndToken();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public abstract String getInternalEndToken();

    public abstract String getInternalBeginToken();

    public abstract String getRelativePath();

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return 1;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return true;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return buffer;
    }

    public Object copyTo(ObjectLocator locator, Object draftCopy, CopyStrategy strategy) {
        if (draftCopy instanceof PFile) {
            ((PFile) draftCopy).setPMarker(pMarker);
            ((PFile) draftCopy).setPArchive(pArchive);
        }
        return draftCopy;
    }

    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof PMarker) {
            pMarker = (PMarker) parent;
        } else if (parent instanceof PArchive) {
            pArchive = (PArchive) parent;
        } else {
            throw new DefectRuntimeException("Wrong Parent [" + parent.getClass().getName() + "]");
        }
    }

}
