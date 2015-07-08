package com.redhat.lightblue.camel;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.guice.CamelModule;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.redhat.lightblue.camel.model.Event;
import com.redhat.lightblue.client.LightblueClient;
import com.redhat.lightblue.client.request.LightblueRequest;

public class TestCamelModule extends CamelModule {

    private final LightblueClient client;
    private final LightblueRequest request;

    public TestCamelModule(LightblueClient client) {
        this(client, null);
    }

    public TestCamelModule(LightblueClient client, LightblueRequest request) {
        super();
        this.client = client;
        this.request = request;
    }

    @Override
    protected void configure() {
        super.configure();

        bind(LightblueClient.class).toInstance(client);
        bind(LightblueRequest.class).toProvider(new Provider<LightblueRequest>() {

            @Override
            public LightblueRequest get() {
                return request;
            }

        });
    }

    @Provides
    Set<RoutesBuilder> routes(Injector injector) {
        Set<RoutesBuilder> set = new HashSet<RoutesBuilder>();
        set.add(new TestInboundRoute());
        set.add(new TestOutboundRoute<Event[]>(Event[].class));
        return set;
    }

}
