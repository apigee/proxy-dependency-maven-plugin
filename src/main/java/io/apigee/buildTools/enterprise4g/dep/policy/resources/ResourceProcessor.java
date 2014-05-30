package io.apigee.buildTools.enterprise4g.dep.policy.resources;


import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;

public interface ResourceProcessor {
    void actOn(File targetDir, Log log) throws IOException;
}
