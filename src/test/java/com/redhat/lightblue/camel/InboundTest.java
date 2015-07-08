package com.redhat.lightblue.camel;

import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import java.net.UnknownHostException;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.redhat.lightblue.client.expression.query.ValueQuery;
import com.redhat.lightblue.client.integration.test.AbstractLightblueClientCRUDController;
import com.redhat.lightblue.client.projection.FieldProjection;
import com.redhat.lightblue.client.request.data.DataFindRequest;

/**
 * Test for {@link TestInboundRoute}.
 *
 * @author mpatercz
 *
 */
public class InboundTest extends AbstractLightblueClientCRUDController {

    public InboundTest() throws Exception {
        super();
    }

    CamelContext context;
    ProducerTemplate template;
    MockEndpoint resultEndpoint;

    @Before
    public void setupCamel() throws Exception {
        // init guice and register the client
        Injector injector = Guice.createInjector(new TestCamelModule(getLightblueClient()));

        // init camel context
        context = injector.getInstance(CamelContext.class);

        // setup template
        template = context.createProducerTemplate();
        template.setDefaultEndpointUri("direct:start");

        resultEndpoint = context.getEndpoint("mock:result", MockEndpoint.class);

        // start camel
        context.start();
    }

    @Before
    public void cleanupLightblueCollections() throws UnknownHostException {
        cleanupMongoCollections("user");
    }

    @Test
    public void testMessageToLightblue() throws Exception {
        String message = Resources.toString(Resources.getResource("./inbound/user-message.xml"), Charsets.UTF_8);
        template.sendBody(message);

        DataFindRequest findRequest = new DataFindRequest("user", null);
        findRequest.where(ValueQuery.withValue("objectType = user"));
        findRequest.select(FieldProjection.includeField("*"));
        Map[] users = getLightblueClient().data(findRequest, Map[].class);

        Assert.assertNotNull(users);
        Assert.assertEquals(2, users.length);

        resultEndpoint.expectedMinimumMessageCount(1);
        resultEndpoint.assertIsSatisfied();
    }

    @After
    public void tearDownCamel() throws Exception {
        context.stop();
    }

    @Override
    public JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{
                loadJsonNode("./inbound/user.json")
        };
    }

}
