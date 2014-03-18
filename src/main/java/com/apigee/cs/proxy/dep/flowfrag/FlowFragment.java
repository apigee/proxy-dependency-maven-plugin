package com.apigee.cs.proxy.dep.flowfrag;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class FlowFragment {
    private final String fragContent;
    private File flowFragment;

    public FlowFragment(File flowFragment) throws IOException {
        this.flowFragment = flowFragment;
        fragContent = FileUtils.readFileToString(flowFragment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlowFragment that = (FlowFragment) o;

        if (!flowFragment.getName().equals(that.flowFragment.getName())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return flowFragment.getName().hashCode();
    }

    public String replace(String proxyContent) {
        String fragment = deriveFragmentName();
        return proxyContent.replace(fragment, fragContent);
    }

    private String deriveFragmentName() {
        final String baseName = FilenameUtils.getBaseName(this.flowFragment.getName());
        return "#" + baseName + "#";
    }
}
