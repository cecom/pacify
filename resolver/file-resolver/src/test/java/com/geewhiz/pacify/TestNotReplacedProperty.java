package com.geewhiz.pacify;

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

import java.io.File;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;
import com.geewhiz.pacify.utils.FileUtils;

public class TestNotReplacedProperty extends TestBase {

    @Test
    public void checkForNotCorrect() {
        File testResourceFolder = new File("src/test/resources/notReplacedPropertyTest");
        File targetResourceFolder = new File("target/test-resources/notReplacedPropertyTest");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/myProperties.properties");
        URL myTestPropertyURL = FileUtils.getFileUrl(myTestProperty);

        Assert.assertTrue("StartPath [" + targetResourceFolder.getPath() + "] doesn't exist!", targetResourceFolder.exists());

        PropertyResolveManager propertyResolveManager = createPropertyResolveManager(myTestPropertyURL);

        Replacer replacer = new Replacer(propertyResolveManager);

        EntityManager entityManager = new EntityManager(targetResourceFolder);
        entityManager.initialize();

        LinkedHashSet<Defect> defects = replacer.doReplacement(entityManager);

        Assert.assertEquals(3, defects.size());
    }

    private PropertyResolveManager createPropertyResolveManager(URL myTestPropertyURL) {
        Set<PropertyResolver> resolverList = new TreeSet<PropertyResolver>();
        FilePropertyResolver filePropertyResolver = new FilePropertyResolver(myTestPropertyURL);
        resolverList.add(filePropertyResolver);

        PropertyResolveManager propertyResolveManager = new PropertyResolveManager(resolverList);
        return propertyResolveManager;
    }
}
