package com.cloud.hypervisor.kvm.resource;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExceptionNoStack;

import java.io.File;
import java.io.IOException;

public class LibvirtXMLTransformer {
    private final String script;
    private final String method;
    private final GroovyScriptEngine gse;
    private final Binding binding = new Binding();

    private static final Logger s_logger = Logger.getLogger(LibvirtXMLTransformer.class);

    public LibvirtXMLTransformer(String path, String script, String method) throws IOException {
        this.script = script;
        this.method = method;
        File full_path = new File(path, script);
        if (!full_path.canRead()) {
            s_logger.warn("Groovy script '" + full_path.toString() + "' is not available. Transformations will not be applied.");
            this.gse = null;
        } else {
            this.gse = new GroovyScriptEngine(path);
        }
    }

    public boolean isInitialized() {
        return this.gse != null;
    }

    public String transform(String xml) throws ResourceException, ScriptException {
        if (!isInitialized()) {
            s_logger.warn("Groovy scripting engine is not initialized. Data transformation skipped.");
            return xml;
        }

        GroovyObject transformer = (GroovyObject) this.gse.run(this.script, binding);
        if (null == transformer) {
            s_logger.warn("Transformer object is not received from script '" + this.script + "'.");
            return xml;
        } else {
            Object[] params = {xml};
            try {
                String transformedXml = (String) transformer.invokeMethod(this.method, params);
                if (null == transformedXml) {
                    s_logger.warn("Transformed XML is NULL. Unable to launch VM with empty libvirt XML specification.");
                    return null;
                }
                s_logger.debug("Transformed XML is:\n" + transformedXml);
                return transformedXml;
            } catch (MissingMethodExceptionNoStack e) {
                s_logger.error("Error occured when calling transform method from groovy script, {}", e);
                return xml;
            }
        }
    }
}
