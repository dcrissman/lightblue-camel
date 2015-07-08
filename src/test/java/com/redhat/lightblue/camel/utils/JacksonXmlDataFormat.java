package com.redhat.lightblue.camel.utils;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 *
 *
 */
public class JacksonXmlDataFormat implements DataFormat {

    private final XmlMapper mapper = new XmlMapper();
    private final Class<?> type;
    private final Config config;

    public JacksonXmlDataFormat() {
        this(new Config(), Object.class);
    }

    public JacksonXmlDataFormat(Config config) {
        this(config, Object.class);
    }

    public JacksonXmlDataFormat(Class<?> type) {
        this(new Config(), type);
    }

    public JacksonXmlDataFormat(Config config, Class<?> type) {
        this.type = type;
        this.config = config;
    }

    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        ObjectWriter writer = mapper.writer();
        if (config.getRootName() != null) {
            writer = writer.withRootName(config.getRootName());
        }

        stream.write(writer.writeValueAsString(graph).getBytes());
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        return mapper.readValue(stream, type);
    }

    public static class Config {
        private String rootName = null;

        public String getRootName() {
            return rootName;
        }

        public Config setRootName(String rootName) {
            this.rootName = rootName;
            return this;
        }

    }

}
