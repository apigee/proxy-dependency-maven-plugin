package com.apigee.cs.proxy.dep.policy;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PolicyDependencyProcessor {
    private final Log log;
    private final File rootDir;
    private File[] proxyDirs;
    private File proxyRootDir;

    public PolicyDependencyProcessor(Log log, File rootDir, File... proxyDirs) {
        this.log = log;
        this.rootDir = rootDir;
        this.proxyDirs = proxyDirs;
    }

    public void processPolicyDependencies(List<Policy> policies) throws IOException {
        List<File> xmlFiles = getAllProxyXMLFiles();
        log.debug("Processing Proxy XML Files: " + xmlFiles);
        processPolicyFiles(xmlFiles, policies);
    }

    private void processPolicyFiles(List<File> xmlFiles, List<Policy> policies) throws IOException {
        HashSet<String> refPolicies = new LinkedHashSet<String>();
        for (File xmlFile : xmlFiles) {
            refPolicies.addAll(getPolicies(FileUtils.readLines(xmlFile)));
        }
        for (Policy policy : policies) {
            policy.actOn(refPolicies,rootDir,log);
        }
        if (!refPolicies.isEmpty()) {
            log.warn("Policies not found: " + refPolicies);
        }
    }

    private List<String> getPolicies(List<String> lines) {
        final ArrayList<String> policies = new ArrayList<String>();
        for (String line : lines) {
            Pattern pattern = Pattern.compile("<Name>(.*)</Name>");
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.find()) {
                policies.add(matcher.group(1));
            }
        }
        return policies;
    }

    private List<File> getAllProxyXMLFiles() {
        ArrayList<File> xmlFiles = new ArrayList<File>();
        for (File proxyDir : proxyDirs) {
            xmlFiles.addAll(FileUtils.listFiles(proxyDir, new String[]{"xml"}, false));
        }
        return xmlFiles;
    }
}
