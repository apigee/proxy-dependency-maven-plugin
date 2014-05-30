package io.apigee.buildTools.enterprise4g.dep.flowfrag;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FlowFragmentProcessor {

    private Log log;
    private File[] proxyDirs;

    public FlowFragmentProcessor(Log log, File... proxyDirs) {
        this.log = log;
        this.proxyDirs = proxyDirs;
    }

    public void processFragments(Collection<FlowFragment> flowFragments) throws IOException {
        List<File> xmlFiles = getAllProxyXMLFiles();
        log.debug("Processing Proxy XML Files: " + xmlFiles);
        replaceFlowFragments(xmlFiles, flowFragments);
    }

    private void replaceFlowFragments(List<File> xmlFiles, Collection<FlowFragment> flowFragments) throws IOException {
        for (File xmlFile : xmlFiles) {
            processFile(xmlFile, flowFragments);
        }
    }

    private void processFile(File xmlFile, Collection<FlowFragment> flowFragments) throws IOException {
        String proxyContent = FileUtils.readFileToString(xmlFile);
        for (FlowFragment flowFragment : flowFragments) {
            proxyContent = flowFragment.replace(proxyContent);
        }
        FileUtils.writeStringToFile(xmlFile, proxyContent);
    }

    private List<File> getAllProxyXMLFiles() {
        ArrayList<File> xmlFiles = new ArrayList<File>();
        for (File proxyDir : proxyDirs) {
            xmlFiles.addAll(FileUtils.listFiles(proxyDir, new String[]{"xml"}, false));
        }
        return xmlFiles;
    }
}
