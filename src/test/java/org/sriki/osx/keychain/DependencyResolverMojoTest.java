package org.sriki.osx.keychain;


import io.apigee.buildTools.enterprise4g.dep.DependencyResolverMojo;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DependencyResolverMojoTest {

    @Rule
    public MojoRule rule = new MojoRule();
    Properties props;
    private File testTempDir;
    private File proxyRoot;
    private File proxyRef;
    private File target;


    private DependencyResolverMojo getResolverMojo() throws Exception {
        File pom = new File("src/test/resources/test-pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        DependencyResolverMojo resolverMojo = (DependencyResolverMojo) rule.lookupMojo("resolve", pom);
        assertNotNull(resolverMojo);
        return resolverMojo;
    }

    @Test
    public void shouldResolveAllDependencies() throws Exception {
        DependencyResolverMojo resolverMojo = getResolverMojo();
        resolverMojo.setProxySrcDir(proxyRoot.getPath());
        resolverMojo.setProxyDestDir(target.getPath());
        resolverMojo.setProxyRefs(new String[]{proxyRef.getPath()});
        resolverMojo.execute();
        final File policiesDir = new File(target.getPath() + "/apiproxy/policies");
        final File resDir = new File(target.getPath() + "/apiproxy/resources");
        final File jsResDir = new File(target.getPath() + "/apiproxy/resources/jsc");
        final File proxiesDir = new File(target.getPath() + "/apiproxy/proxies");
        assertThat(policiesDir.exists(), is(true));
        assertThat(resDir.exists(), is(true));
        assertThat(jsResDir.exists(), is(true));
        assertThat(proxiesDir.exists(), is(true));
        assertThat(new File(policiesDir, "common_oauth_fault.flowfrag").exists(), is(false));
        assertThat(new File(resDir, "common_oauth_fault.flowfrag").exists(), is(true));
        assertThat(policiesDir.list().length, is(44));
        assertPolicies(policiesDir);
        assertJSFiles(jsResDir);
    }

    private void assertJSFiles(File jsResDir) {
        String[] expectedFiles = new String[]{"js_conv_age_to_dob.js", "js_prevent_req_path_copy.js",
                "js_set_oauth_cred.js", "js_set_user_attrs.js", "js_add_trusted_headers.js", "js_check_accesstoken.js",
                "js_setkeyandauth.js", "js_setup_splunk_vars.js","crypto_js","moment_js"};
        Arrays.sort(expectedFiles);
        String[] actualFiles = jsResDir.list();
        Arrays.sort(actualFiles);
        assertThat(actualFiles, is(expectedFiles));
        final String[] expectedCryptoFiles = {"core-min.js","enc-base64-min.js","sha512-min.js","x64-core-min.js",};
        Arrays.sort(expectedCryptoFiles);
        actualFiles = new File(jsResDir,"crypto_js").list();
        Arrays.sort(actualFiles);
        assertThat(actualFiles, is(expectedCryptoFiles));
        final String[] expectedMomentFiles = {"moment.min.js"};
        Arrays.sort(expectedMomentFiles);
        actualFiles = new File(jsResDir,"moment_js").list();
        Arrays.sort(actualFiles);
        assertThat(actualFiles, is(expectedMomentFiles));

    }

    private void assertPolicies(File policiesDir) {
        String[] expectedPolicies = new String[]{"assign_add_qp_request.xml",
                "assign_build_get_token_response.xml", "assign_build_json_response.xml",
                "assign_build_xml_response.xml", "assign_handle_cookies_accept.xml", "assign_refresh_token_json_response.xml",
                "assign_set_local_form_variables.xml", "assign_set_local_header_variables.xml",
                "assign_set_local_query_variables.xml", "assign_set_user_create_target.xml", "assign_set_user_login_target.xml", "assign_transform_request.xml",
                "extract_refresh_token_params.xml", "extract_user_data.xml",
                "extract_user_res_data.xml",  "fault_accept_json_not_found.xml",
                "fault_invalid_secret.xml", "fault_user_creation.xml",  "js_prevent_req_path_copy.xml",
                "js_set_oauth_cred.xml",  "keymap_get_token_link.xml", "keymap_upd_token_link.xml",
                "oauthv2_gen_accesstoken.xml", "oauthv2_refresh_accesstoken.xml", "service_callout_check.xml",
                "verify_apikey_clientid.xml", "assign_set_variables.xml",  "oauthv2_verify_accesstoken.xml",
                "js_check_accesstoken.xml", "keymap_get_kmscredentials.xml", "assign_set_kms_auth.xml", "service_callout_kms.xml",

                "js_setkeyandauth.xml",  "oauthv2_token_get_attr.xml",
                "js_setup_splunk_vars.xml", "log_splunk.xml", "quota_rate_limit.xml", "spike_arrest_by_clientid.xml", "keymap_get_cred.xml",
                "assign_alter_queryparams_headers.xml", "js_add_trusted_headers.xml", "assign_remove_x_forward_headers.xml",
                "assign_init_variables.xml", "fault_appId_not_found.xml"
        };
        Arrays.sort(expectedPolicies);
        final String[] actualPolicies = policiesDir.list();
        Arrays.sort(actualPolicies);
        assertThat(actualPolicies, is(expectedPolicies));
    }

    @Before
    public void setUp() throws Exception {
        testTempDir = FileUtils.getTempDirectory();
        FileUtils.forceMkdir(testTempDir);
        FileUtils.copyDirectory(new File("src/test/resources"), testTempDir);
        System.out.println(testTempDir.getAbsolutePath());
        proxyRoot = new File(testTempDir.getPath() + "/Proxy");
        proxyRef = new File(testTempDir.getPath() + "/ProxyRef");
        target = new File(testTempDir.getPath() + "/target");

    }

    @After
    public void tearDown() throws Exception {
        //FileUtils.deleteDirectory(testTempDir);
    }
}
