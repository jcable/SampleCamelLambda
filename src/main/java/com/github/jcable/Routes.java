package com.github.jcable;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;

/**
 * Camel route definitions.
 */
public class Routes extends RouteBuilder {

    public Routes() {
    }

    @Override
    public void configure() throws Exception {

	from("direct:input").to("log:input")
        .process(new Processor() {
              public void process(Exchange exchange) throws Exception {
                InputObject input = exchange.getIn().getBody(InputObject.class);
        	String result = input.getGreeting() + " " + input.getName();
        	OutputObject out = new OutputObject();
        	out.setResult(result);
		out.setRequestId("aws-request-1");
		exchange.getIn().setBody(out);
              }
        });

    }
}
