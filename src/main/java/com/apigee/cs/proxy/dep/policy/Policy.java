package com.apigee.cs.proxy.dep.policy;

import com.apigee.cs.proxy.dep.policy.resources.ResourceProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class Policy {
    private File policy;
    private ResourceProcessor[] resourceProcessors;

    public Policy(File policy, ResourceProcessor... resourceProcessors) throws IOException {
        this.policy = policy;
        this.resourceProcessors = resourceProcessors;
    }

    public void actOn(HashSet<String> refPolicies, File rootDir, Log log) throws IOException {
        final String baseName = FilenameUtils.getBaseName(policy.getName());
        if (!refPolicies.contains(baseName)) {
            return;
        }
        refPolicies.remove(baseName);
        copy(rootDir, log);
    }

    private void copy(File rootDir, Log log) throws IOException {
        final File destDir = new File(rootDir.getPath() + "/apiproxy/policies");
        log.debug(String.format("Copying file %s to %s", policy.getAbsolutePath(), destDir.getAbsolutePath()));
        FileUtils.copyFileToDirectory(policy, destDir);
        for (ResourceProcessor resourceProcessor : resourceProcessors) {
            resourceProcessor.actOn(new File(rootDir.getPath() + "/apiproxy/resources/jsc"), log);
        }
    }

}
