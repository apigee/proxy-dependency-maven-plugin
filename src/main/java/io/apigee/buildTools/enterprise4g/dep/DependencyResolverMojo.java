package io.apigee.buildTools.enterprise4g.dep;


import io.apigee.buildTools.enterprise4g.dep.flowfrag.FlowFragmentProcessor;
import io.apigee.buildTools.enterprise4g.dep.policy.PolicyDependencyProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Resolves the dependency given the proxy root directory.
 */
@Mojo(name = "resolve", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class DependencyResolverMojo
        extends AbstractMojo {

    /**
     * Root directory of the proxy whose dependencies have to be
     * resolved.This is by default set to "."
     */
    @Parameter(required = false, defaultValue = ".")
    private String proxySrcDir;

    /**
     * Root directory under which proxy files have to placed after resolution.
     * This is by default set to "./target"
     */
    @Parameter(required = false, defaultValue = "./target")
    private String proxyDestDir;


    /**
     * List of proxies to use for dependency resolution.
     * The policy references are searched in the textual order
     * given in the configuration of the plugin. With first match
     * of the policy, resolution stops.
     * <p/>
     * Javascript resources files referenced by the proxy are
     * resolved relative to the policy file found in the referenced proxies.
     * The Javascript files must be present in the same proxy as the referencing
     * policy file.
     */
    @Parameter(required = false)
    private String[] proxyRefs;

    private File policyDir;
    private File proxyDir;
    private File targetDir;
    private File resDir;
    private File jsResDir;


    public void execute()
            throws MojoExecutionException {
        initTargetDirs();
        List<String> refs = new ArrayList<String>();
        refs.add(proxySrcDir);
        refs.addAll(Arrays.asList(proxyRefs));
        final ProxyRefProcessor proxyRefProcessor = new ProxyRefProcessor(refs, getLog());
        processFlowFragments(proxyRefProcessor);
        processPolicyDependencies(proxyRefProcessor);
    }

    private void processPolicyDependencies(ProxyRefProcessor proxyRefProcessor) throws MojoExecutionException {
        try {
            new PolicyDependencyProcessor(getLog(), new File(proxyDestDir), proxyDir, targetDir).processPolicyDependencies(proxyRefProcessor.policies());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void processFlowFragments(ProxyRefProcessor proxyRefProcessor) throws MojoExecutionException {
        try {
            new FlowFragmentProcessor(getLog(), proxyDir, targetDir).processFragments(proxyRefProcessor.flowFragments());
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    public void setProxyDestDir(String proxyDestDir) {
        this.proxyDestDir = proxyDestDir;
    }

    private void initTargetDirs() throws MojoExecutionException {
        try {
            copyProxySrcToTarget();
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        this.policyDir = new File(proxyDestDir + "/apiproxy/policies");
        this.policyDir.mkdirs();
        this.proxyDir = new File(proxyDestDir + "/apiproxy/proxies");
        this.targetDir = new File(proxyDestDir + "/apiproxy/targets");
        this.resDir = new File(proxyDestDir + "/apiproxy/resources");
        this.jsResDir = new File(proxyDestDir + "/apiproxy/resources/jsc");
        this.jsResDir.mkdirs();
    }

    private void copyProxySrcToTarget() throws IOException {
        final File destDir = new File(proxyDestDir);
        FileUtils.copyDirectory(new File(proxySrcDir), destDir);
        cleanupDestDir(destDir);
    }

    private void cleanupDestDir(File destDir) {
        final Collection<File> filesToBeDeleted = FileUtils.listFiles(destDir, new NotFileFilter(new SuffixFileFilter(".xml")),
                getNonResourcesDirFilter());
        final Log log = getLog();
        for (File file : filesToBeDeleted) {
            log.debug("Deleting un-necesssary file: " + file.getPath());
            FileUtils.deleteQuietly(file);
        }
    }

    private IOFileFilter getNonResourcesDirFilter() {
        return new AbstractFileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.getPath().contains("/apiproxy/resources");
            }
        };
    }

    public void setProxyRefs(String[] proxyRefs) {
        this.proxyRefs = proxyRefs;
        this.proxyRefs = this.proxyRefs == null ? new String[0] : this.proxyRefs;

    }

    public void setProxySrcDir(String proxySrcDir) {
        this.proxySrcDir = proxySrcDir;
    }
}
