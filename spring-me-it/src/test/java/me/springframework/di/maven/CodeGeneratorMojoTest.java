/**
 * Copyright (C) 2009 Original Authors
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Spring ME; see the file COPYING. If not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module. An independent module is a module which is not derived from or
 * based on this library. If you modify this library, you may extend this
 * exception to your version of the library, but you are not obligated to
 * do so. If you do not wish to do so, delete this exception statement
 * from your version.
 */
package me.springframework.di.maven;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class CodeGeneratorMojoTest extends AbstractMojoTestCase {


//    /**
//     * A convenience method for getting a configured {@link me.springframework.di.maven.BeanFactoryGeneratorMojo}
//     * instance.
//     *
//     * @param test
//     *            The name of the test. (Used to resolve the pom.xml file from a corresponding directory.)
//     * @return A configured instance of the {@link BeanFactoryGeneratorMojo}.
//     * @throws Exception
//     *             If an instance of the {@link BeanFactoryGeneratorMojo} cannot be
//     *             constructed from the file name passed in.
//     */
//    private BeanFactoryGeneratorMojo getMojo(String test) throws Exception {
//        return (BeanFactoryGeneratorMojo) lookupMojo("generate", getBasedir()
//                + "/src/test/resources/" + test + "/pom.xml");
//    }

    /**
     * Tests everything is ok for builds running directly from source folders only.
     */
    @Test
    public void testSmokeTest() throws Exception {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/it-smoke");
        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("com.tomtom.di.maven.its", "it-spring-me-smoke", "1.0", "jar");
        verifier.executeGoal("compile");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(fileToString(testDir, "target"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me/com/tomtom/di/maven/its/itsample/BeanFactory.java"));
        verifier.resetStreams();
    }
    
    /**
     * Tests Strings are escaped properly.
     */
    @Test
    public void testStringEscaping() throws IOException, VerificationException {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/it-smoke");
        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("com.tomtom.di.maven.its", "it-spring-me-string-esc", "1.0", "jar");
        verifier.executeGoal("compile");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(fileToString(testDir, "target"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me/com/tomtom/di/maven/its/itsample/BeanFactory.java"));
        verifier.resetStreams();
    }

    /**
     * Tests everything is ok for builds running directly from source folders only.
     */
    @Test
    public void testCheckedExceptions() throws IOException, VerificationException {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/it-checked-exceptions");
        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("com.tomtom.di.maven.its", "it-spring-me-checked-exceptions", "1.0", "jar");
        verifier.executeGoal("compile");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(fileToString(testDir, "target"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me/com/tomtom/di/maven/its/itsample/BeanFactory.java"));
        verifier.resetStreams();
    }

    /**
     * Tests everything is ok for builds running directly from source folders only.
     */
    @Test
    public void testMaps() throws IOException, VerificationException {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/it-maps");
        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("com.tomtom.di.maven.its", "it-spring-me-maps", "1.0", "jar");
        verifier.executeGoal("compile");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(fileToString(testDir, "target"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me/com/tomtom/di/maven/its/itsample/BeanFactory.java"));
        verifier.resetStreams();
    }

    /**
     * Tests everything is ok for builds running directly from source folders only.
     */
    @Test
    public void testCheckedExceptionsForProperties() throws IOException, VerificationException {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/it-checked-exceptions-properties");
        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("com.tomtom.di.maven.its", "it-spring-me-checked-exceptions-properties", "1.0", "jar");
        verifier.executeGoal("compile");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(fileToString(testDir, "target"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me/com/tomtom/di/maven/its/itsample/BeanFactory.java"));
        verifier.resetStreams();
    }

    /**
     * Tests everything is ok in case there are dependencies on other libraries, and those libraries
     * are not yet installed yet. (So we depend on commons-beanutils, which in turn depends on
     * commons-collections. In our Spring configuration file we are going to include a dependency on
     * a instance of a class of commons-collections.)
     */
    @Test
    public void testRecursiveDependencies() throws IOException, VerificationException {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/it-recursive-dependencies");
        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("com.tomtom.di.maven.its", "it-spring-me-recursive-dependencies", "1.0", "jar");
        verifier.executeGoal("compile");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(fileToString(testDir, "target"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me/com/tomtom/di/maven/its/itsample/BeanFactory.java"));
        verifier.resetStreams();
    }

    @Test
    public void testPrototypeScope() throws IOException, VerificationException {
        File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/it-prototype");
        Verifier verifier = new Verifier(testDir.getAbsolutePath());
        verifier.deleteArtifact("com.tomtom.di.maven.its", "it-spring-me-prototype", "1.0", "jar");
        verifier.executeGoal("compile");
        verifier.verifyErrorFreeLog();
        verifier.assertFilePresent(fileToString(testDir, "target"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me"));
        verifier.assertFilePresent(fileToString(testDir, "target/generated-sources/spring-me/com/tomtom/di/maven/its/itsample/BeanFactory.java"));
        verifier.resetStreams();
    }
    
    private String fileToString(File basedir, String name) {
        return new File(basedir, name).getAbsolutePath();
    }

}
