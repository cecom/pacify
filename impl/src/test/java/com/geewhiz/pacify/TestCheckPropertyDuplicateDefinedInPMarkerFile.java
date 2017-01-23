/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.geewhiz.pacify;

import java.util.Collections;
import java.util.LinkedHashSet;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.checks.impl.CheckPropertyDuplicateDefinedInPacifyFile;
import com.geewhiz.pacify.defect.Defect;

public class TestCheckPropertyDuplicateDefinedInPMarkerFile extends TestBase {

    @Test
    public void checkForNotCorrect() {
        String testFolder = "checkPropertyDuplicateDefinedInPMarkerFile/wrong";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(Collections.<String, String> emptyMap()),
                new CheckPropertyDuplicateDefinedInPacifyFile());

        Assert.assertEquals(1, defects.size());
    }

    @Test
    public void checkForCorrect() {
        String testFolder = "checkPropertyDuplicateDefinedInPMarkerFile/correct";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(Collections.<String, String> emptyMap()),
                new CheckPropertyDuplicateDefinedInPacifyFile());

        Assert.assertEquals(0, defects.size());
    }

}
