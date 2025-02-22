package org.apache.maven.plugins.deploy;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="jerome@coffeebreaks.org">Jerome Lacoste</a>
 */
public class DeployFileMojoUnitTest
{
    MockDeployFileMojo mojo;
    Parent parent;

    @Before
    public void setUp()
    {
        Model pomModel = new Model();
        pomModel.setPackaging( null );

        parent = new Parent();
        parent.setGroupId( "parentGroup" );
        parent.setArtifactId( "parentArtifact" );
        parent.setVersion( "parentVersion" );

        mojo = new MockDeployFileMojo( pomModel );
    }

    @After
    public void tearDown()
    {
        mojo = null;
    }

    static class MockDeployFileMojo extends DeployFileMojo {
        private Model model;

        public MockDeployFileMojo(Model model) {
            this.model = model;
        }

        public void setModel(Model model) {
            this.model = model;
        }

        protected Model readModel(File pomFile)
        {
            return model;
        }
    }

    @Test
    public void testProcessPomFromPomFileWithParent1()
    {
        mojo.setPomFile( new File( "foo.bar" ) );

        setMojoModel( mojo.model, null, null, null, null, parent );

        try {
            mojo.initProperties();
        } catch (MojoExecutionException expected) {
            assertTrue( true ); // missing artifactId and packaging
        }

        checkMojoProperties("parentGroup", null, "parentVersion", null);
    }

    @Test
    public void testProcessPomFromPomFileWithParent2()
    {
        mojo.setPomFile( new File( "foo.bar" ) );
        setMojoModel( mojo.model, null, "artifact", null, null, parent );

        try {
            mojo.initProperties();
        } catch (MojoExecutionException expected) {
            assertTrue( true ); // missing packaging
        }

        checkMojoProperties("parentGroup", "artifact", "parentVersion", null );

    }

    @Test
    public void testProcessPomFromPomFileWithParent3()
    {
        mojo.setPomFile( new File( "foo.bar" ) );
        setMojoModel( mojo.model, null, "artifact", "version", null, parent );

        try {
            mojo.initProperties();
        } catch (MojoExecutionException expected) {
            assertTrue( true ); // missing version and packaging
        }

        checkMojoProperties( "parentGroup", "artifact", "version", null );
    }

    @Test
    public void testProcessPomFromPomFileWithParent4() throws MojoExecutionException
    {
        mojo.setPomFile( new File( "foo.bar" ) );
        setMojoModel( mojo.model, null, "artifact", "version", "packaging", parent );

        mojo.initProperties();

        checkMojoProperties("parentGroup", "artifact", "version", "packaging");
    }

    @Test
    public void testProcessPomFromPomFileWithParent5() throws MojoExecutionException
    {
        mojo.setPomFile( new File( "foo.bar" ) );
        setMojoModel( mojo.model, "group", "artifact", "version", "packaging", parent );

        mojo.initProperties();

        checkMojoProperties("group", "artifact", "version", "packaging");
    }

    @Test
    public void testProcessPomFromPomFileWithParent6() throws MojoExecutionException
    {
        mojo.setPomFile( new File( "foo.bar" ) );
        setMojoModel( mojo.model, "group", "artifact", "version", "packaging", null );

        mojo.initProperties();

        checkMojoProperties("group", "artifact", "version", "packaging");

    }

    @Test
    public void testProcessPomFromPomFileWithOverrides() throws MojoExecutionException
    {
        mojo.setPomFile( new File( "foo.bar" ) );
        setMojoModel( mojo.model, "group", "artifact", "version", "packaging", null );

        mojo.setGroupId( "groupO" );
        mojo.setArtifactId( "artifactO" );
        mojo.setVersion( "versionO" );
        mojo.setPackaging( "packagingO" );

        mojo.initProperties();

        checkMojoProperties("groupO", "artifactO", "versionO", "packagingO");
    }

    private void checkMojoProperties(final String expectedGroup, final String expectedArtifact, final String expectedVersion, final String expectedPackaging) {
        assertEquals( expectedGroup, mojo.getGroupId() );
        assertEquals( expectedArtifact, mojo.getArtifactId() );
        assertEquals( expectedVersion, mojo.getVersion() );
        assertEquals( expectedPackaging, mojo.getPackaging() );
    }

    private void setMojoModel(Model model, String group, String artifact, String version, String packaging, Parent parent ) {
        model.setGroupId( group );
        model.setArtifactId( artifact );
        model.setVersion( version );
        model.setPackaging( packaging );
        model.setParent( parent );
    }

}
