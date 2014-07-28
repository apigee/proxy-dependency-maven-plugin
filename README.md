
Proxy Dependency Maven Plugin
=======================
Flow and Policy reuse is one of the good practices to follow during proxy development. The philosophy adheres to the DRY principle. However the reuse is currently not supported out of the box in Apigee Edge.

One of the approaches used for reuse is to have a set of common proxies, containing flow fragments/polices, and reference these in other proxies. This maven plugin helps in implementing the approach. The dependency resolution through references is completely handled by the maven plugin.

Need For Dependency Resolution
---------------------------------------------

An Apigee proxy has a standard directory structure shown below

```
├── apiproxy 					
│   ├── SampleApiProxy.xml	 
│   ├── policies				
│   ├── proxies				
│   │   └── default.xml
│   ├── resources			
│   │   └── jsc				
│   └── targets				
│       └── default.xml
```

The `proxies` and `targets` directories contains the proxy endpoints and target endpoint files. The policies used in these endpoints are expected to be in `policies` directory. Language based policies expect the corresponding language resources in the `resources` directory. For example, Javascript resources are expected to be in `jsc` directory under `resources` directory.

An API program for an organization typically involves development of multiple proxies. In most of the scenarios, a lot of policies and policy flows are same. Building each proxy as a directory structure above requires duplication of these policy files and flows. This is acceptable at deployment time; however during the development time it is useful to develop the policies in a single place and have a tool copy the policies over to the `policies` directory of the API proxies that reference them. This plugin is that tool !!

Dependency Resolution
--------------------------------
Two kinds of dependency resolution is supported by this tool they are - `Policy Resolution` and `Flow Resolution`

####Policy Resolution

An API proxy's proxy endpoint or target endpoint can reference policies that are common. These referenced policies are copied by the plugin to the `policies` directory of the API proxy. If the policy referenced is language based, the corresponding resources are copied into the `resources` directory. Thus, this feature enables policy re-use.

####Flow Resolution

Flow resolution is supported by defining a new type of file called `flowfrag`(Flow fragment) file.
Flow fragment is a text file containing a list of reusable steps with extension `.flowfrag` . An example below

```xml
<Step>
    <Name>policy1</Name>
</Step>
<Step>
    <Name>policy2</Name>
</Step>
```

Flow fragment can be referenced by proxy or target endpoint using the format `#flow-fragment-base-filename#` without the `.flowfrag ` extension. Such a reference results in macro style substitution of the content of the flow fragment file, followed by policy resolution of the all the policies referenced by the fragment. Thus enabling flow re-use across proxies

**Note:** *Flow fragments files should be present only in `proxies` directory of the referenced proxies.*

Dependency Resolution With Example
---------------------------------------------------

###Example

####Common Proxy 
Common proxy contains the policies and flow fragments that can be re-used.

```
CommonProxy
├── apiproxy 					
│   ├── CommonProxy.xml	 
│   ├── policies			
│   │   └── spike_arrest_by_clientid.xml	
│   │   └── quota_rate_limit.xml	
│   │   └── js_add_trusted_headers.xml	
│   ├── proxies				
│   │   └── default.xml
│   │   └── spike_arrest_and_quota.flowfrag
│   ├── resources			
│   │   └── jsc
│   │   │      └── js_add_trusted_headers.js
│   └── targets				
│       └── default.xml
```
`spike_arrest_and_quota.flowfrag ` is a flow fragment that can be re-used. 

####Filename: `spike_arrest_and_quota.flowfrag`
```xml
<Step>
    <Name>spike_arrest_by_clientid</Name>
</Step>
<Step>
    <Name>quota_rate_limit</Name>
</Step>
```

####Client Validation API
Client validation proxy implements client validation API.

```
ClientValidationAPI
├── apiproxy 					
│   ├── ClientValidationAPI.xml	 
│   ├── policies			
│   │   └── assign_set_local_header_variables.xml
│   │   └── fault_invalid_secret.xml	
│   │   └── js_prevent_req_path_copy.xml	
│   ├── proxies				
│   │   └── dummy_proxy_endpoint.xml
│   │   └── validation.flowfrag
│   ├── resources			
│   │   └── jsc
│   │   │      └── js_prevent_req_path_copy.js
│   └── targets				
│       └── dummy_target_endpoint.xml
```

The Client Validation API itself consists of flow fragment `validation.flowfrag` that is used across multiple endpoints - `dummy_proxy_endpoint.xml `and  `dummy_target_endpoint.xml` within the same proxy. *This flow re-use within the s same proxy itself.*

####Filename: `validation.flowfrag`
```xml
<Step>
     <Condition>(verifyapikey.verify_apikey_clientid.client_secret != local_secret)</Condition>
      <Name>fault_invalid_secret</Name>
</Step>
```

####Filename:  `dummy_proxy_endpoint.xml`
```xml
<TargetEndpoint name="dummyEndPonint">
    <Flows>
        <Flow name="endpointwith_flowfrag_ref">
            <Request>
              <Step>
                    <Name>assign_set_local_header_variables</Name>
                </Step>
                	#validation#
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

####Filename:  `pom.xml` in Client Validation API
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

###Dependency Resolution process for  `dummy_proxy_endpoint.xml`
When the dependency plugin is run on the proxy containing the above file -

*flow fragment* resolution process kicks-in

+ **\#validation\#** tag is found in the file `dummy_proxy_endpoint.xml`. A flow fragment named `validation.flowfrag` is searched in the `proxies` directory of same proxy - Client Validation API. The file with such as name is found; the  `#validation#` is replaced by the contents of the file in `dummy_proxy_endpoint.xml`

+ **\#spike_arrest_and_quota\#** tag is found next.  A flow fragment named `spike_arrest_and_quota.flowfrag` is searched in the `proxies` directory of same proxy- Client Validation API. No such file exists. Now the list of referenced proxies is obtained; the list contains only `CommonProxy`. A flow fragment named `spike_arrest_and_quota.flowfrag` is searched in the `proxies` directory of  `CommonProxy`. The file is found; the ` #spike_arrest_and_quota#` is replaced by the contents of that file in `dummy_proxy_endpoint.xml`

The resultant file after flow fragment resolution is as follows.

####Filename:  `dummy_proxy_endpoint.xml` after flow fragment resolution
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


*policy resolution* kicks-in next

+  ***assign_set_local_header_variables***  policy is searched for in `policies` directory of same proxy -  Client Validation API. It's found, nothing needs to be done.

+  ***fault_invalid_secret***  policy is searched for in `policies` directory of same proxy -  Client Validation API. It's found, nothing needs to be done.

+ ***spike_arrest_by_clientid***  policy is searched for in `policies` directory of same proxy -  Client Validation API.It's not found. Now the list of referenced proxies is obtained; the list contains only `CommonProxy`. The policy is searched in the `policies` directory of  `CommonProxy`. The file is found; and the same file is copied to the `policies` directory under the `proxyDestDir` directory specified in the maven `pom` file.

+ ***quota_rate_limit***  policy is searched for in policies directory of same proxy -  Client Validation API.Its not found. Now the list of referenced proxies is obtained; the list contains only `CommonProxy`. The policy is searched in the `policies` directory of  `CommonProxy`. The file is found; and the same file is copied to the `policies` directory under the `proxyDestDir` directory specified in the maven `pom` file.

+ ***js_add_trusted_headers***  policy is searched for in `policies` directory of same proxy -  Client Validation API.It's not found. Now the list of referenced proxies is obtained; the list contains only `CommonProxy`. The policy is searched in the `policies` directory of  `CommonProxy`. The file is found; and the same file is copied to the `policies` directory under the `proxyDestDir` directory specified in the maven `pom` file. This policy also references the JS resource file `js_add_trusted_headers.js` - this file is also copied to the `resources/jsc` directory under `proxyDestDir` directory 

+ ***js_prevent_req_path_copy***  policy is searched for in `policies` directory of same proxy -  Client Validation API. Its found, nothing needs to be done.

**Note: Please note all the resolution performed is not in-place, files are read from `proxySrcDir` directory  resolved and copied to `proxyDestDir` directory specified in `pom` file**

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
    Optional directory name under which the proxy files have to placed after resolution. Resolved proxy files will have their flow fragments replaced and required policy files copied into `policies` directory. This is by default set to `./target`

 * **`proxyRefs`**
      List of proxies to use for dependency resolution. The policy references
      are searched in the textual order provided in the configuration of the
      plugin. With first match of the policy, resolution stops.

      JavaScript resources files referenced by the proxy are resolved relative
      to the policy file found in the referenced proxies. The JavaScript files
      must be present in the same proxy as the referencing policy file.
      
Resolution Process Pseudo code
--------------------------------------------
``` 
Read Endpoint XML 

For Each "#FlowFragment#' do
	Find FlowFragment.flowfrag file in proxies directory of same proxy
	if flowFragFound then
		Replace #FlowFragment# with the content of the file.
	else
		For Each 'Proxy References' do
			Find FlowFragment.flowfrag file in proxies directory of proxy reference.
			if flowFragFound then
				Replace #FlowFragment# with the content of the file.
				break
			end if
		done
		if flow frag not resolved then
			Flag Error
		endif
	endif
done

For Each "#PolicyName#' do
	Find PolicyName.xml file in policies directory of same proxy.
	if PolicyNotFound then
		For Each 'Proxy References' do
			Find PolicyName.xml file in policies directory of proxy reference.
			if PolicyFound then
				Copy PolicyName.xml file to policies directory of the referencing proxy
				break;
			end if
		done
		If Policy not resolved then
			Flag Error
		endif
	endif
done

Repeat the entire process for each of the proxy and target endpoints.
```

Current State
------------------
The proxy dependency maven plugin copies all the policies referenced. It also copies JavaSrcipt resources referenced by the Javascript policies too.

We need to implement support to copy other extension policies like Java and Python.

Please feel free to contribute the same. If you need assistance do get in touch with  [Srikanth Seshadri](sseshadri@apigee.com)  or [Priyanky Thomas](priyanky@apigee.com).

People Involved
------------------------
The idea of proxy reuse was proposed and defined by [Steve Richardson](srichardson@apigee.com)and [Priyanky Thomas](priyanky@apigee.com). The initial idea and implementation of the plugin is done by [Srikanth Seshadri](sseshadri@apigee.com)



