package com.github.jcable;

import javax.inject.Inject;
import javax.inject.Named;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import org.apache.camel.CamelContext;

@Named("test")
public class Lambda implements RequestHandler<InputObject, OutputObject> {

    @Inject
    CamelContext camelContext;

    @Override
    public OutputObject handleRequest(InputObject input, Context context) {
        return camelContext.createProducerTemplate().requestBody("direct:input", input, OutputObject.class);
    }
}
