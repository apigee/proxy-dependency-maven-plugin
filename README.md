Proxy Dependency Maven Plugin
=======================
Flow and Policy reuse is one of the good practices to follow during proxy development. The philosophy adheres to the DRY principle. However the reuse is currently not support out of the box in Apigee Edge.

One of the approaches used for resuse is to have a set of common proxies, containing flow fragments/polices, and reference these in other proxies. This maven plugin helps in implementing the approach. The dependency resolution through references is completely handled by the maven plugin.

Concepts
------------
Dependency resolution by this plugin warrents following some conventions. This is particularly applicable for flow fragment resolution.

Flow fragment is a text file containg a list of resusable steps with extension *.flowfrag* . Following is an example of the same.

*Filename:  spike_arrest_and_quota.flowfrag*
<pre>
&lt;Step&gt;
    &lt;Name&gt;spike_arrest_by_clientid&lt;/Name&gt;
&lt;/Step&gt;
&lt;Step&gt;
    &lt;Name&gt;quota_rate_limit&lt;/Name&gt;
&lt;/Step&gt;
</pre>

Above flow can be referenced by using the name of the flow fragment file without extension, Format: *#basename#* .<br/>
Following is a example of referencing a flow fragment.

*Filename:  dummy_endpoint.xml*
<pre>
&lt;TargetEndpoint name=&quot;dummyEndPonint&quot;&gt;
    &lt;Flows&gt;
        &lt;Flow name=&quot;endpointwith_flowfrag_ref&quot;&gt;
            &lt;Request&gt;
              &lt;Step&gt;
                    &lt;Name&gt;assign_set_local_header_variables&lt;/Name&gt;
                &lt;/Step&gt;
                &lt;Step&gt;
                    &lt;Condition&gt;(verifyapikey.verify_apikey_clientid.client_secret != local_secret)&lt;/Condition&gt;
                    &lt;Name&gt;fault_invalid_secret&lt;/Name&gt;
                &lt;/Step&gt;
               __#spike_arrest_and_quota#__
                &lt;Step&gt;
                    &lt;Name&gt;js_add_trusted_headers&lt;/Name&gt;
                &lt;/Step&gt;
                &lt;Step&gt;
                    &lt;Name&gt;js_prevent_req_path_copy&lt;/Name&gt;
                &lt;/Step&gt;
            &lt;/Request&gt;
            &lt;Response/&gt;
        &lt;/Flow&gt;
    &lt;/Flows&gt;
&lt;/TargetEndpoint&gt;
</pre>
When the dependency plugin is run on the proxy containing the above file, the reference will be replaced by the content of the flow fragment file. The output would be the following
*Filename:  dummy_endpoint.xml*
<pre>
&lt;TargetEndpoint name=&quot;dummyEndPonint&quot;&gt;
    &lt;Flows&gt;
        &lt;Flow name=&quot;endpointwith_flowfrag_ref&quot;&gt;
            &lt;Request&gt;
              &lt;Step&gt;
                    &lt;Name&gt;assign_set_local_header_variables&lt;/Name&gt;
                &lt;/Step&gt;
                &lt;Step&gt;
                    &lt;Condition&gt;(verifyapikey.verify_apikey_clientid.client_secret != local_secret)&lt;/Condition&gt;
                    &lt;Name&gt;fault_invalid_secret&lt;/Name&gt;
                &lt;/Step&gt;
               __&lt;Step&gt;
    				&lt;Name&gt;spike_arrest_by_clientid&lt;/Name&gt;
			&lt;/Step&gt;
			&lt;Step&gt;
    				&lt;Name&gt;quota_rate_limit&lt;/Name&gt;
			&lt;/Step&gt;__
                &lt;Step&gt;
                    &lt;Name&gt;js_add_trusted_headers&lt;/Name&gt;
                &lt;/Step&gt;
                &lt;Step&gt;
                    &lt;Name&gt;js_prevent_req_path_copy&lt;/Name&gt;
                &lt;/Step&gt;
            &lt;/Request&gt;
            &lt;Response/&gt;
        &lt;/Flow&gt;
    &lt;/Flows&gt;
&lt;/TargetEndpoint&gt;
</pre>

Usage
---------
Following is an example usage of the plugin.
<pre>
&lt;plugin&gt;
    &lt;groupId&gt;com.apigee.cs&lt;/groupId&gt;
    &lt;artifactId&gt;proxy-dependency-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;1.0&lt;/version&gt;
    &lt;executions&gt;
        &lt;execution&gt;
            &lt;goals&gt;
                 &lt;goal&gt;resolve&lt;/goal&gt;
             &lt;/goals&gt;
             &lt;configuration&gt;
                  &lt;proxySrcDir&gt;.&lt;/proxySrcDir&gt;
                  &lt;proxyDestDir&gt;./target&lt;/proxyDestDir&gt;
                  &lt;proxyRefs&gt;
                      &lt;proxyRef&gt;../CommonProxy&lt;/proxyRef&gt;
                  &lt;/proxyRefs&gt;
             &lt;/configuration&gt;
         &lt;/execution&gt;
    &lt;/executions&gt;
&lt;/plugin&gt;
</pre>
The parameters for the plugin are as follows

 * proxySrcDir (Default: .) <br/>
      Optional directory name of the proxy whose dependencies have to be resolved.This
      is by default set to '.'
      
 * proxyDestDir (Default: ./target)<br/>
      Optional directory name under which the proxy files have to placed after resolution. 	This is by default set to './target'
      
 * proxyRefs<br/>
      List of proxies to use for dependency resolution. The policy references
      are searched in the textual order provided in the configuration of the
      plugin. With first match of the policy, resolution stops.
      
      Javascript resources files referenced by the proxy are resolved relative
      to the policy file found in the referenced proxies. The Javascript files
      must be present in the same proxy as the referencing policy file.

Current State
------------------
The proxy dependency maven plugin copies all the policies referenced. It also copies Javasrcipt resources referenced by the Javascript policies too.

We need to implement support to copy other extension policies like Java and Python.

Please feel free to contribute the same. If you need assistance do get in touch with  [Srikanth Seshadri](sseshadri@apigee.com)  or [Priyanky Thomas](priyanky@apigee.com).

People Involved
------------------------
The idea of proxy reuse was proposed and defined by [Steve Richardson](srichardson@apigee.com)and [Priyanky Thomas](priyanky@apigee.com). The initial idea and implementation of the plugin is done by [Srikanth Seshadri](sseshadri@apigee.com) 


