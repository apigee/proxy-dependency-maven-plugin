
Proxy Dependency Maven Plugin
=======================
Flow and Policy reuse is one of the good practices to follow during proxy development. The philosophy adheres to the DRY principle. However the reuse is currently not supported out of the box in Apigee Edge.

One of the approaches used for reuse is to have a set of common proxies, containing flow fragments/polices, and reference these in other proxies. This maven plugin helps in implementing the approach. The dependency resolution through references is completely handled by the maven plugin.

Concepts
------------
Dependency resolution by this plugin warrants following some conventions. This is particularly applicable for flow fragment resolution.

Flow fragment is a text file containing a list of reusable steps with extension `.flowfrag` . Following is an example of the same.

####Filename: `spike_arrest_and_quota.flowfrag`
```xml
<Step>
    <Name>spike_arrest_by_clientid</Name>
</Step>
<Step>
    <Name>quota_rate_limit</Name>
</Step>
```

Above flow can be referenced by using the name of the flow fragment file without extension, Format: `#basename#`
Following is a example of referencing a flow fragment.

####Filename:  `dummy_endpoint.xml`
```xml
<TargetEndpoint name="dummyEndPonint">
    <Flows>
        <Flow name="endpointwith_flowfrag_ref">
            <Request>
              <Step>
                    <Name>assign_set_local_header_variables</Name>
                </Step>
                <Step>
                    <Condition>(verifyapikey.verify_apikey_clientid.client_secret != local_secret)</Condition>
                    <Name>fault_invalid_secret</Name>
                </Step>
                   #spike_arrest_and_quota#
                <Step>
                    <Name>js_add_trusted_headers</Name>
                </Step>
                <Step>
                    <Name>js_prevent_req_path_copy</Name>
                </Step>
            </Request>
            <Response/>
        </Flow>
    </Flows>
</TargetEndpoint>
```
When the dependency plugin is run on the proxy containing the above file, the reference will be replaced by the content of the flow fragment file. The output would be the following
####*Filename:  `dummy_endpoint.xml`*
```xml
<TargetEndpoint name="dummyEndPonint">
    <Flows>
        <Flow name="endpointwith_flowfrag_ref">
            <Request>
              <Step>
                    <Name>assign_set_local_header_variables</Name>
                </Step>
                <Step>
                    <Condition>(verifyapikey.verify_apikey_clientid.client_secret != local_secret)</Condition>
                    <Name>fault_invalid_secret</Name>
                </Step>
                <Step>
               <Name>spike_arrest_by_clientid</Name>
                </Step>
                <Step>
                        <Name>quota_rate_limit</Name>
                </Step>
                <Step>
                    <Name>js_add_trusted_headers</Name>
                </Step>
                <Step>
                    <Name>js_prevent_req_path_copy</Name>
                </Step>
            </Request>
            <Response/>
        </Flow>
    </Flows>
</TargetEndpoint>
```

Usage
---------
Following is an example usage of the plugin.
```xml
<plugin>
    <groupId>io.apigee.build-tools.enterprise4g</groupId>
    <artifactId>proxy-dependency-maven-plugin</artifactId>
    <version>2.0.0</version>
    <executions>
        <execution>
            <goals>
                 <goal>resolve</goal>
             </goals>
             <configuration>
                  <proxySrcDir>.</proxySrcDir>
                  <proxyDestDir>./target</proxyDestDir>
                  <proxyRefs>
                      <proxyRef>../CommonProxy</proxyRef>
                  </proxyRefs>
             </configuration>
         </execution>
    </executions>
</plugin>
```
The parameters for the plugin are as follows

 * **`proxySrcDir`** (Default: `.`)
    Optional directory name of the proxy whose dependencies have to be resolved.
    This is by default set to `.`

 * **`proxyDestDir`** (Default: `./target`)
    Optional directory name under which the proxy files have to placed after resolution.  This is by default set to `./target`

 * **`proxyRefs`**
      List of proxies to use for dependency resolution. The policy references
      are searched in the textual order provided in the configuration of the
      plugin. With first match of the policy, resolution stops.

      JavaScript resources files referenced by the proxy are resolved relative
      to the policy file found in the referenced proxies. The JavaScript files
      must be present in the same proxy as the referencing policy file.

Current State
------------------
The proxy dependency maven plugin copies all the policies referenced. It also copies JavaSrcipt resources referenced by the Javascript policies too.

We need to implement support to copy other extension policies like Java and Python.

Please feel free to contribute the same. If you need assistance do get in touch with  [Srikanth Seshadri](sseshadri@apigee.com)  or [Priyanky Thomas](priyanky@apigee.com).

People Involved
------------------------
The idea of proxy reuse was proposed and defined by [Steve Richardson](srichardson@apigee.com)and [Priyanky Thomas](priyanky@apigee.com). The initial idea and implementation of the plugin is done by [Srikanth Seshadri](sseshadri@apigee.com)



