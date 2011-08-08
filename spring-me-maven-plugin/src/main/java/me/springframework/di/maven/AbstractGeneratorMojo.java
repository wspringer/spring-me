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

import com.thoughtworks.qdox.JavaDocBuilder;
import me.springframework.di.Configuration;
import me.springframework.di.spring.AutowiringAugmentation;
import me.springframework.di.spring.QDoxAugmentation;
import me.springframework.di.spring.SinkAugmentation;
import me.springframework.di.spring.SpringConfigurationLoader;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.project.artifact.MavenMetadataSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * A base class for all plugins processing a {@link Configuration} loaded from Spring.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public abstract class AbstractGeneratorMojo extends AbstractMojo {

    /**
     * The project in which the plugin is included.
     * 
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * Local Maven repository.
     * 
     * @parameter expression="${localRepository}"
     */
    private ArtifactRepository localRepository;

    /**
     * @component
     */
    protected ArtifactFactory artifactFactory;

    /**
     * The factories to be generated. 
     * 
     * @parameter
     */
    private List<Factory> factories = new ArrayList<Factory>();

    /**
     * The name of the bean factory to be generated.
     * 
     * @parameter
     */
    private String className;

    /**
     * @parameter expression="{dotFile}"
     */
    private File dotFile;

    /**
     * The XML file containing the definitions of your beans.
     * 
     * @parameter
     */
    private File contextFile;

    // JavaDoc inherited
    final public void execute() throws MojoExecutionException, MojoFailureException {
        for (BeanFactory factory : getBeanFactories()) {
            Configuration configuration = load(new FileSystemResource(factory.getContextFile()));
            process(configuration, factory);
        }
    }

    private List<BeanFactory> getBeanFactories() {
        List<BeanFactory> result = new ArrayList<BeanFactory>();
        if (factories != null && factories.size() > 0) {
            // Treat the other configuration as defaults
            for (Factory factoryConfig : factories) {
                result.add(new ChainingBeanFactory(factoryConfig, new MojoBeanFactory()));
            }
        } else {
            result.add(new MojoBeanFactory());
        }
        return result;
    }

    private Configuration load(Resource resource) throws MojoExecutionException {
        JavaDocBuilder builder = createJavaDocBuilder();
        QDoxAugmentation augmentation = new QDoxAugmentation(builder);
        augmentation.setLoggingKitAdapter(new MavenLoggingKitAdapter(getLog()));
        AutowiringAugmentation autowire = new AutowiringAugmentation(builder);
        SinkAugmentation sink = new SinkAugmentation();
        SpringConfigurationLoader loader = new SpringConfigurationLoader(augmentation, autowire, sink);
        return loader.load(resource);
    }

    /**
     * Processes the configuration passed in.
     * 
     * @param config The configuration to be processed.
     */
    public abstract void process(Configuration config, BeanFactory factory) throws MojoExecutionException, MojoFailureException;

    /**
     * Produces the {@link JavaDocBuilder} used to analyze classes and source files.
     * 
     * @return The {@link JavaDocBuilder} used to analyze classes and source files.
     * @throws MojoExecutionException If we fail to produce the {@link JavaDocBuilder}. (Fail
     *             early.)
     */
    @SuppressWarnings("unchecked")
    private JavaDocBuilder createJavaDocBuilder() throws MojoExecutionException {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(project.getBuild().getSourceDirectory()));
        // Quick and dirty hack for adding Classloaders with the definitions of
        // classes the sources depend upon.
        try {
            Set<Artifact> artifacts = MavenMetadataSource.createArtifacts(artifactFactory, project.getDependencies(), Artifact.SCOPE_RUNTIME,
                    new ArtifactFilter() {

                        public boolean include(Artifact artifact) {
                            return true;
                        }

                    }, project);
            List<URL> urls = new ArrayList<URL>();
            for (Artifact artifact : artifacts) {
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Adding artifact " + artifact.getId());
                }
                if (Artifact.SCOPE_SYSTEM.equals(artifact.getScope())) {
                    urls.add(artifact.getFile().toURL());
                } else {
                    urls.add(new File(localRepository.getBasedir(), localRepository.pathOf(artifact)).toURL());
                }
            }
            URL[] template = new URL[0];
            URLClassLoader loader = new URLClassLoader(urls.toArray(template));
            builder.getClassLibrary().addClassLoader(loader);
        } catch (InvalidDependencyVersionException idve) {
            idve.printStackTrace();
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Malformed artifact URLs.");
        }
        return builder;
    }

    /**
     * A project reference, providing a context for the MOJO execution.
     * 
     * @return The project reference, providing a context for execution of the Mojo.
     */
    protected MavenProject getProject() {
        return project;
    }

    private class MojoBeanFactory implements BeanFactory {

        public String getClassName() {
            return className;
        }

        public File getDotFile() {
            return dotFile;
        }

        public File getContextFile() {
            return contextFile;
        }
        
    }

}
