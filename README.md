# SampleCamelLambda
Using Apache Quarkus to deploy an Apache Camel Route in an AWS Lambda

A small example to demonstrate using Apache Camel in AWS Lambda.

The Camel Route is trivial:

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

Adapting the route to the Lambda makes use of a Quarkus RequestHandler.

	public class Lambda implements RequestHandler<InputObject, OutputObject> {

	    @Inject
	    CamelContext camelContext;

	    @Override
	    public OutputObject handleRequest(InputObject input, Context context) {
		return camelContext.createProducerTemplate().requestBody("direct:input", input, OutputObject.class);
	    }
	}

We use CDI to inject the CamelContext into the request handler and then use the camelContext object to create a
ProducerTemplate which can be used to invoke the Camel route.

The Maven project for the example is derived from the Quarkus lambda example with Apache Camel dependencies from the Camel Quarkus examples.
