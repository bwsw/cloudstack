package com.cloud.hypervisor.kvm.resource;

import groovy.util.ResourceException;
import groovy.util.ScriptException;
import junit.framework.TestCase;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExceptionNoStack;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class LibvirtXMLTransformerTest extends TestCase {

    private final String source = "<xml />";
    private final String dir = "/tmp";
    private final String script = "xml-transform-test.groovy";
    private final String method = "transform";
    private final String methodNull = "transform2";
    private final String testImpl = "package groovy\n" +
            "\n" +
            "class BaseTransform {\n" +
            "    String transform(String xml) {\n" +
            "        return xml + xml\n" +
            "    }\n" +
            "    String transform2(String xml) {\n" +
            "        return null\n" +
            "    }\n" +
            "}\n" +
            "\n" +
            "new BaseTransform()\n" +
            "\n";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PrintWriter pw = new PrintWriter(new File(dir, script));
        pw.println(testImpl);
        pw.close();
    }

    @Override
    protected void tearDown() throws Exception {
        new File(dir, script).delete();
        super.tearDown();
    }

    public void testTransform() throws IOException, ResourceException, ScriptException {
        LibvirtXMLTransformer t = new LibvirtXMLTransformer(dir, script, method);
        assertEquals(t.isInitialized(), true);
        String result = t.transform(source);
        assertEquals(result, source + source);
    }

    public void testWrongMethod() throws IOException, ResourceException, ScriptException {
        LibvirtXMLTransformer t = new LibvirtXMLTransformer(dir, script, "methodX");
        assertEquals(t.isInitialized(), true);
        assertEquals(t.transform(source), source);
    }

    public void testNullMethod() throws IOException, ResourceException, ScriptException {
        LibvirtXMLTransformer t = new LibvirtXMLTransformer(dir, script, methodNull);
        assertEquals(t.isInitialized(), true);
        assertEquals(t.transform(source), null);
    }

    public void testWrongScript() throws IOException, ResourceException, ScriptException {
        LibvirtXMLTransformer t = new LibvirtXMLTransformer(dir, "wrong-script.groovy", method);
        assertEquals(t.isInitialized(), false);
        assertEquals(t.transform(source), source);
    }

    public void testWrongDir() throws IOException, ResourceException, ScriptException {
        LibvirtXMLTransformer t = new LibvirtXMLTransformer("/" + UUID.randomUUID().toString() + "-dir", script, method);
        assertEquals(t.isInitialized(), false);
        assertEquals(t.transform(source), source);
    }
}
