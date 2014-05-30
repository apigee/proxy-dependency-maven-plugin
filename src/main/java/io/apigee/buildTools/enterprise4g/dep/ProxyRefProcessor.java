package io.apigee.buildTools.enterprise4g.dep;

import io.apigee.buildTools.enterprise4g.dep.flowfrag.FlowFragment;
import io.apigee.buildTools.enterprise4g.dep.policy.Policy;
import io.apigee.buildTools.enterprise4g.dep.policy.resources.js.JavaScriptResourceProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProxyRefProcessor {
    private final List<File> flowFragments;
    private final List<File> policies;
    private Log log;

    public ProxyRefProcessor(List<String> proxyRefs, Log log) {
        this.log = log;
        this.flowFragments = buildFlowFragement(proxyRefs);
        this.policies = buildPolicies(proxyRefs);
    }

    private List<File> buildPolicies(List<String> proxyRefs) {
        ArrayList<File> policies = new ArrayList<File>();

        for (String proxyRef : proxyRefs) {
            policies.addAll(allPoliciesIn(proxyRef));
        }
        return policies;
    }

    private List<File> allPoliciesIn(String proxyRef) {
        List<File> policies = allFilesIn(proxyRef + "/apiproxy/policies", "xml");
        log.debug("Found policies: " + policies);
        return policies;
    }

    private List<File> allFilesIn(String dir, String extension) {
        ArrayList<File> filesList = new ArrayList<File>();
        final File filesDir = new File(dir);
        if (!filesDir.exists()) {
            log.warn(filesDir + " does not exists");
            return filesList;
        }
        filesList.addAll(FileUtils.listFiles(filesDir, new String[]{extension}, false));
        return filesList;
    }

    private List<File> buildFlowFragement(List<String> proxyRefs) {
        ArrayList<File> flowFrags = new ArrayList<File>();
        for (String proxyRef : proxyRefs) {
            flowFrags.addAll(allFlowFrags(proxyRef));
        }
        return flowFrags;
    }

    private List<File> allFlowFrags(String proxyRef) {
        List<File> flowFrags = allFilesIn(proxyRef + "/apiproxy/proxies", "flowfrag");
        log.debug("Found flowfrags: " + flowFrags);
        return flowFrags;
    }

    public Collection<FlowFragment> flowFragments() throws IOException {
        ArrayList<FlowFragment> flowFrags = new ArrayList<FlowFragment>();
        for (File flowFragment : flowFragments) {
            flowFrags.add(new FlowFragment(flowFragment));
        }
        return flowFrags;
    }

    public List<Policy> policies() throws IOException {
        ArrayList<Policy> policies = new ArrayList<Policy>();
        for (File policy : this.policies) {
            policies.add(new Policy(policy, new JavaScriptResourceProcessor(policy)));
        }
        return policies;
    }
}
