package com.apigee.cs.proxy.dep.policy.resources;


import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;

public interface ResourceProcessor {
    void actOn(File targetDir, Log log) throws IOException;
}
