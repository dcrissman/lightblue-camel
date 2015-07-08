package com.redhat.lightblue.camel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.dozer.DozerBeanMapperConfiguration;
import org.apache.camel.converter.dozer.DozerTypeConverterLoader;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import com.redhat.lightblue.camel.utils.JacksonXmlDataFormat;
import com.redhat.lightblue.camel.utils.LightblueErrorVerifier;
import com.redhat.lightblue.camel.utils.LightblueResponseTransformer;

public class TestOutboundRoute<T> extends RouteBuilder {

    private final Class<T> type;

    public TestOutboundRoute(Class<T> type) {
        this.type = type;
    }

    @Override
    public void configure() throws Exception {
        DozerBeanMapperConfiguration mapper = new DozerBeanMapperConfiguration();
        mapper.setMappingFiles(Arrays.asList(new String[]{"outbound/dozer/EventToMap.xml"}));
        new DozerTypeConverterLoader(getContext(), mapper);

        from("lightblue://outboundPollingTest")
                .bean(new LightblueErrorVerifier())
                .bean(new LightblueResponseTransformer<T>(type))
                .split(body(), new AggregationStrategy() {

                    @Override
                    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
                        Object newBody = newExchange.getIn().getBody();
                        ArrayList<Object> list = null;
                        if (oldExchange == null) {
                            list = new ArrayList<Object>();
                            list.add(newBody);
                            newExchange.getIn().setBody(list);
                            return newExchange;
                        } else {
                            list = oldExchange.getIn().getBody(ArrayList.class);
                            list.add(newBody);
                            return oldExchange;
                        }
                    }
                })
                .convertBodyTo(Map.class)
                .end()
                .marshal(new JacksonXmlDataFormat())
                .to("mock:result");
    }
}
