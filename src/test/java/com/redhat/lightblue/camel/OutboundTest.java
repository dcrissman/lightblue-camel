package com.redhat.lightblue.camel;

import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.redhat.lightblue.client.expression.query.ValueQuery;
import com.redhat.lightblue.client.integration.test.AbstractLightblueClientCRUDController;
import com.redhat.lightblue.client.projection.FieldProjection;
import com.redhat.lightblue.client.request.data.DataFindRequest;

/**
 * Test for {@link TestOutboundRoute}.
 *
 * @author mpatercz
 *
 */
public class OutboundTest extends AbstractLightblueClientCRUDController {

    public OutboundTest() throws Exception {
        super();
    }

    @Override
    public JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{loadJsonNode("./outbound/event.json")};
    }

    CamelContext context;
    MockEndpoint resultEndpoint;

    @Before
    public void setupCamel() throws Exception {
        // polling request
        DataFindRequest findRequest = new DataFindRequest("event", "1.0.0");
        findRequest.where(ValueQuery.withValue("processed = false"));
        findRequest.select(FieldProjection.includeFieldRecursively("*"));

        // init guice and register the client and polling request
        Injector injector = Guice.createInjector(new TestCamelModule(getLightblueClient(), findRequest));

        // init camel context
        context = injector.getInstance(CamelContext.class);

        resultEndpoint = context.getEndpoint("mock:result", MockEndpoint.class);

        // start camel
        context.start();
    }

    @Test
    public void testMessageFromLightblue() throws Exception {
        // load events
        loadData("event", "1.0.0", "./outbound/data/insert/events.json");

        resultEndpoint.expectedMessageCount(2);
        resultEndpoint.expectedBodiesReceived("<events xmlns=\"\"><item><status>false</status><ID>2</ID><full_name>Something happened</full_name></item><item><status>false</status><ID>3</ID><full_name>Something else happened</full_name></item></events>");
        resultEndpoint.assertIsSatisfied();
    }

    @After
    public void tearDownCamel() throws Exception {
        context.stop();
    }

}
