/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.locationtech.jtstest;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.util.Assert;
import org.locationtech.jtstest.testrunner.SimpleReportWriter;
import org.locationtech.jtstest.testrunner.TestEngine;

import junit.framework.TestCase;

public class CoreGeometryXMLTest extends TestCase {
    public CoreGeometryXMLTest(String name) {
        super(name);
    }

    public void testUnit() {
        testFiles("src/test/resources/testxml/general", "src/test/resources/testxml/validate");
    }
    
//    public void testExternal() {
//        testFiles("../core/src/test/resources/testxml/external");
//    }

//    public void testFailure() {
//        testFiles("../core/src/test/resources/testxml/failure");
//    }


//    public void testRobust() {
//        testFiles("../core/src/test/resources/testxml/robust");
//    }

//    public void testStmlf() {
//        testFiles("../core/src/test/resources/testxml/stmlf");
//    }

    private void testFiles(String... directoryName) {
        GeometryFactory gf = new GeometryFactory(new PrecisionModel(),0);
        GeometryFactory.setDefaultFactory(gf);
        TestEngine engine = new TestEngine();
        List<File> testFiles = new ArrayList<File>();
        for (String dirName : directoryName) {
          testFiles.addAll( filenames(new File(dirName)) );
        }
        engine.setTestFiles(testFiles);
        engine.run();
        SimpleReportWriter reportWriter = new SimpleReportWriter(false);
        reportWriter.writeReport(engine);
        System.out.println(reportWriter.writeReport(engine));
        
        boolean failures = engine.getParseExceptionCount() + engine.getFailedCount() + engine.getExceptionCount() > 0;
        assertEquals(failures, false);
        GeometryFactory.initDefaultFactory();
    }

    static FilenameFilter XML_FILTER = new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.endsWith(".xml");
      }
    };
    
    private static List<File> filenames(File directory) {
        Assert.isTrue(directory.isDirectory());
        File[] files = directory.listFiles(XML_FILTER);

        return Arrays.asList(files);
    }
}
