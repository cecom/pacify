package com.geewhiz.pacify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarInputStream;

import javax.xml.bind.JAXBException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.checks.impl.CheckCorrectArchiveType;
import com.geewhiz.pacify.defect.ArchiveDuplicateDefinedInPMarkerDefect;
import com.geewhiz.pacify.defect.ArchiveTypeNotImplementedDefect;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.defect.FilterNotFoundDefect;
import com.geewhiz.pacify.defect.NoPlaceholderInTargetFileDefect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.defect.PlaceholderNotDefinedDefect;
import com.geewhiz.pacify.defect.PropertyDuplicateDefinedInPMarkerDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.property.resolver.HashMapPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;
import com.geewhiz.pacify.utils.LoggingUtils;

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

public class TestArchive {

    @Test
    public void checkJar() throws ArchiveException, IOException {
        Logger logger = LogManager.getLogger(TestArchive.class.getName());
        LoggingUtils.setLogLevel(logger, Level.INFO);

        File testResourceFolder = new File("src/test/resources/testArchive/correct/jar");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/jar");

        LinkedHashSet<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.jar");
        File outputArchive = new File(targetResourceFolder, "package/archive.jar");

        JarInputStream in = new JarInputStream(new FileInputStream(new File(testResourceFolder, "package/archive.jar")));
        JarInputStream out = new JarInputStream(new FileInputStream(outputArchive));

        Assert.assertNotNull("SRC jar should contain the manifest as first entry", in.getManifest());
        Assert.assertNotNull("RESULT jar should contain the manifest as first entry", out.getManifest());

        in.close();
        out.close();

        checkResultIsAsExpected(outputArchive, expectedArchive);

        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkJarInEar() throws ArchiveException, IOException {
        Logger logger = LogManager.getLogger(TestArchive.class.getName());
        LoggingUtils.setLogLevel(logger, Level.INFO);

        File testResourceFolder = new File("src/test/resources/testArchive/correct/jarInEar");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/jarInEar");

        LinkedHashSet<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/some.ear");
        File outputArchive = new File(targetResourceFolder, "package/some.ear");

        // TODO: does not work with archive in archive
        // checkResultIsAsExpected(outputArchive, expectedArchive);

        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkJarWhereTheSourceIsntAJarPerDefinition() throws ArchiveException, IOException {
        Logger logger = LogManager.getLogger(TestArchive.class.getName());
        LoggingUtils.setLogLevel(logger, Level.ERROR);

        File testResourceFolder = new File("src/test/resources/testArchive/correct/jarWhereSourceIsntAJarPerDefinition");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/jarWhereSourceIsntAJarPerDefinition");

        LinkedHashSet<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.jar");
        File outputArchive = new File(targetResourceFolder, "package/archive.jar");

        JarInputStream in = new JarInputStream(new FileInputStream(new File(testResourceFolder, "package/archive.jar")));
        JarInputStream out = new JarInputStream(new FileInputStream(outputArchive));

        Assert.assertNull("SRC jar should be a jar which is packed via zip, so the first entry isn't the manifest.", in.getManifest());
        Assert.assertNotNull("RESULT jar should contain the manifest as first entry", out.getManifest());

        in.close();
        out.close();

        checkResultIsAsExpected(outputArchive, expectedArchive);

        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkTar() throws ArchiveException, IOException {
        File testResourceFolder = new File("src/test/resources/testArchive/correct/tar");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/tar");

        LinkedHashSet<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.tar");
        File outputArchive = new File(targetResourceFolder, "package/archive.tar");

        checkResultIsAsExpected(outputArchive, expectedArchive);
        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkZip() throws ArchiveException, IOException {
        File testResourceFolder = new File("src/test/resources/testArchive/correct/zip");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/zip");

        LinkedHashSet<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.zip");
        File outputArchive = new File(targetResourceFolder, "package/archive.zip");

        checkResultIsAsExpected(outputArchive, expectedArchive);
        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkBigZip() throws ArchiveException, IOException {
        File testResourceFolder = new File("src/test/resources/testArchive/correct/bigZip");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/bigZip");

        LinkedHashSet<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.zip");
        File outputArchive = new File(targetResourceFolder, "package/archive.zip");

        checkResultIsAsExpected(outputArchive, expectedArchive);
        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkUnkownArchiveType() throws JAXBException {
        File packagePath = new File("target/test-classes/testArchive/wrong/unkownArchiveType/package");

        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();
        EntityManager entityManager = createEntityManager(packagePath, defects);

        List<PMarker> pMarkers = entityManager.getPMarkers();

        Assert.assertEquals(1, pMarkers.size());

        CheckCorrectArchiveType checker = new CheckCorrectArchiveType();
        defects.addAll(checker.checkForErrors(entityManager, pMarkers.get(0)));

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect ArchiveTypeNotImplementedDefect", ArchiveTypeNotImplementedDefect.class, defects.iterator().next().getClass());
    }

    @Test
    public void checkDuplicateArchiveEntry() {
        File packagePath = new File("target/test-classes/testArchive/wrong/duplicateEntry/package");

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(packagePath);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect ArchiveTypeNotImplementedDefect", ArchiveDuplicateDefinedInPMarkerDefect.class, defects.iterator().next().getClass());
    }

    @Test
    public void checkNotReplacedProperty() {
        File testResourceFolder = new File("target/test-classes/testArchive/wrong/notReplacedProperty");
        File targetResourceFolder = new File("target/test-resources/testArchive/wrong/notReplacedProperty");

        LinkedHashSet<Defect> result = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 2, defects.size());
        Assert.assertEquals("We expect NotReplacedPropertyDefect", NotReplacedPropertyDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect NotReplacedPropertyDefect", NotReplacedPropertyDefect.class, defects.get(1).getClass());
        Assert.assertEquals("We expect missing property notReplacedProperty", "notReplacedProperty",
                ((NotReplacedPropertyDefect) defects.get(0)).getPropertyId());
        Assert.assertEquals("We expect missing property notReplacedProperty", "notReplacedProperty",
                ((NotReplacedPropertyDefect) defects.get(1)).getPropertyId());
    }

    @Test
    public void checkTargetFileDoesNotExist() {
        File packagePath = new File("target/test-classes/testArchive/wrong/targetFileDoesNotExist/package");

        LinkedHashSet<Defect> result = createPrepareAndExecuteValidator(packagePath);

        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect FileDoesNotExistDefect", FileDoesNotExistDefect.class, defects.get(0).getClass());
    }

    @Test
    public void checkPlaceholderDoesNotExist() {
        File packagePath = new File("target/test-classes/testArchive/wrong/placeholderDoesNotExist/package");

        LinkedHashSet<Defect> result = createPrepareAndExecuteValidator(packagePath);

        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 2, defects.size());
        Assert.assertEquals("We expect NoPlaceholderInTargetFileDefect", NoPlaceholderInTargetFileDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect PlaceholderNotDefinedDefect.", PlaceholderNotDefinedDefect.class, defects.get(1).getClass());
        Assert.assertEquals("We expect missingProperty", "missingProperty", ((NoPlaceholderInTargetFileDefect) defects.get(0)).getPProperty().getName());
    }

    @Test
    public void checkDuplicatePropertyEntry() {
        File packagePath = new File("target/test-classes/testArchive/wrong/duplicatePropertyEntry/package");

        LinkedHashSet<Defect> result = createPrepareAndExecuteValidator(packagePath);

        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 2, defects.size());
        Assert.assertEquals("We expect PropertyDuplicateDefinedInPMarkerDefect", PropertyDuplicateDefinedInPMarkerDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect PlaceholderNotDefinedDefect.", PlaceholderNotDefinedDefect.class, defects.get(1).getClass());
        Assert.assertEquals("We expect missingProperty", "foobar2", ((PropertyDuplicateDefinedInPMarkerDefect) defects.get(0)).getPProperty().getName());
    }

    @Test
    public void checkWrongPacifyFilter() {
        File packagePath = new File("target/test-classes/testArchive/wrong/wrongPacifyFilter/package");

        LinkedHashSet<Defect> result = createPrepareAndExecuteValidator(packagePath);

        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 2, defects.size());
        Assert.assertEquals("We expect FilterNotFoundDefect", FilterNotFoundDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect PlaceholderNotDefinedDefect.", PlaceholderNotDefinedDefect.class, defects.get(1).getClass());
        Assert.assertEquals("We expect missing.filter.class", "missing.filter.class", ((FilterNotFoundDefect) defects.get(0)).getPFile().getFilterClass());
    }

    private LinkedHashSet<Defect> createPrepareAndExecuteValidator(File packagePath) {
        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        EntityManager entityManager = createEntityManager(packagePath, defects);

        Validator validator = new Validator(prm);
        validator.enableMarkerFileChecks();
        validator.setPackagePath(packagePath);
        defects.addAll(validator.validateInternal(entityManager));

        return defects;
    }

    private EntityManager createEntityManager(File packagePath, LinkedHashSet<Defect> defects) {
        EntityManager entityManager = new EntityManager(packagePath);
        defects.addAll(entityManager.initialize());
        return entityManager;
    }

    private LinkedHashSet<Defect> createPrepareAndExecutePacify(File testResourceFolder, File targetResourceFolder) {
        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        File packagePath = new File(targetResourceFolder, "package");

        EntityManager entityManager = createEntityManager(packagePath, defects);

        Replacer replacer = new Replacer(prm);
        replacer.setPackagePath(packagePath);

        defects.addAll(replacer.doReplacement(entityManager));
        return defects;
    }

    private PropertyResolveManager getPropertyResolveManager(HashMapPropertyResolver hpr) {
        hpr.addProperty("foobar1", "foobar1Value");
        hpr.addProperty("foobar2", "foobar2Value");

        Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
        propertyResolverList.add(hpr);
        PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
        return prm;
    }

    private void checkResultIsAsExpected(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
        archiveContainsEntries(replacedArchive, expectedArchive);
        archiveDoesNotContainAdditionEntries(replacedArchive, expectedArchive);
    }

    private void archiveContainsEntries(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        FileInputStream expectedIS = new FileInputStream(expectedArchive);
        ArchiveInputStream expectedAIS = factory.createArchiveInputStream(new BufferedInputStream(expectedIS));
        ArchiveEntry expectedEntry = null;
        while ((expectedEntry = expectedAIS.getNextEntry()) != null) {
            FileInputStream replacedIS = new FileInputStream(replacedArchive);
            ArchiveInputStream replacedAIS = factory.createArchiveInputStream(new BufferedInputStream(replacedIS));

            ArchiveEntry replacedEntry = null;
            boolean entryFound = false;
            while ((replacedEntry = replacedAIS.getNextEntry()) != null) {
                Assert.assertNotNull("We expect an entry.", replacedEntry);
                if (!expectedEntry.getName().equals(replacedEntry.getName())) {
                    continue;
                }
                entryFound = true;
                if (expectedEntry.isDirectory()) {
                    Assert.assertTrue("we expect a directory", replacedEntry.isDirectory());
                    break;
                }

                ByteArrayOutputStream expectedContent = readContent(expectedAIS);
                ByteArrayOutputStream replacedContent = readContent(replacedAIS);

                Assert.assertEquals("Content should be same of entry " + expectedEntry.getName(), expectedContent.toString("UTF-8"),
                        replacedContent.toString("UTF-8"));
                break;
            }

            replacedIS.close();
            Assert.assertTrue("Entry [" + expectedEntry.getName() + "] in the result archive expected.", entryFound);
        }

        expectedIS.close();
    }

    private void archiveDoesNotContainAdditionEntries(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        FileInputStream replacedIS = new FileInputStream(replacedArchive);
        ArchiveInputStream replacedAIS = factory.createArchiveInputStream(new BufferedInputStream(replacedIS));
        ArchiveEntry replacedEntry = null;
        while ((replacedEntry = replacedAIS.getNextEntry()) != null) {
            FileInputStream expectedIS = new FileInputStream(expectedArchive);
            ArchiveInputStream expectedAIS = factory.createArchiveInputStream(new BufferedInputStream(expectedIS));

            ArchiveEntry expectedEntry = null;
            boolean entryFound = false;
            while ((expectedEntry = expectedAIS.getNextEntry()) != null) {
                Assert.assertNotNull("We expect an entry.", expectedEntry);
                if (!replacedEntry.getName().equals(expectedEntry.getName())) {
                    continue;
                }
                entryFound = true;
                break;
            }

            expectedIS.close();
            Assert.assertTrue("Entry [" + replacedEntry.getName() + "] is not in the expected archive. This file shouldn't exist.", entryFound);
        }

        replacedIS.close();

    }

    private ByteArrayOutputStream readContent(ArchiveInputStream ais) throws IOException {
        byte[] content = new byte[2048];
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(result);

        int len;
        while ((len = ais.read(content)) != -1) {
            bos.write(content, 0, len);
        }
        bos.close();
        content = null;

        return result;
    }
}
