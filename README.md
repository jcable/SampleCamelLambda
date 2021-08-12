# SampleCamelLambda
Using RedHat Quarkus to deploy an Apache Camel Route in an AWS Lambda

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

To run the example:

    git clone https://github.com/jcable/SampleCamelLambda.git
    cd SampleCamelLambda
    mvn clean package
    cd target
    cp ../payload.json .
    export LAMBDA_ROLE_ARN=arn:aws:iam::<myaccount>:role/<a-suitable-existing-role>
    ./manage.sh create
    ./manage.sh invoke

You should see something like:

Invoking function
++ aws lambda invoke response.txt --cli-binary-format raw-in-base64-out --function-name Samplecamellambda --payload file://payload.json --log-type Tail --query LogResult --output text
++ base64 --decode
START RequestId: b9722842-ab37-4939-85e5-8d5987de6562 Version: $LATEST
__  ____  __  _____   ___  __ ____  ______ 
--/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
-/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2021-08-12 15:57:53,665 INFO  [org.apa.cam.qua.cor.CamelBootstrapRecorder] (main) bootstrap runtime: org.apache.camel.quarkus.main.CamelMainRuntime
2021-08-12 15:57:54,163 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) Routes startup summary (total:1 started:1)
2021-08-12 15:57:54,163 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main)     Started route1 (direct://input)
2021-08-12 15:57:54,163 INFO  [org.apa.cam.imp.eng.AbstractCamelContext] (main) Apache Camel 3.9.0 (camel-1) started in 237ms (build:0ms init:178ms start:59ms)
2021-08-12 15:57:54,237 INFO  [io.quarkus] (main) SampleCamelLambda 1.0-SNAPSHOT on JVM (powered by Quarkus 1.13.0.Final) started in 2.261s. 
2021-08-12 15:57:54,241 INFO  [io.quarkus] (main) Profile prod activated. 
2021-08-12 15:57:54,241 INFO  [io.quarkus] (main) Installed features: [amazon-lambda, camel-core, camel-direct, camel-log, camel-support-common, cdi]
2021-08-12 15:57:54,642 INFO  [input] (main) Exchange[ExchangePattern: InOut, BodyType: com.github.jcable.InputObject, Body: com.github.jcable.InputObject@61a5b4ae]
END RequestId: b9722842-ab37-4939-85e5-8d5987de6562
REPORT RequestId: b9722842-ab37-4939-85e5-8d5987de6562	Duration: 492.07 ms	Billed Duration: 493 ms	Memory Size: 256 MB	Max Memory Used: 144 MB	Init Duration: 2525.03 ms	
{"result":"hello Bill","requestId":"aws-request-1"}%                                                                                                                                             cablej01@MC-S104758 target % ./manage.sh invoke
Invoking function
++ aws lambda invoke response.txt --cli-binary-format raw-in-base64-out --function-name Samplecamellambda --payload file://payload.json --log-type Tail --query LogResult --output text
++ base64 --decode
START RequestId: cd6cd879-22b9-4ac8-ac5b-751d1cdd000f Version: $LATEST
2021-08-12 15:57:59,348 INFO  [input] (main) Exchange[ExchangePattern: InOut, BodyType: com.github.jcable.InputObject, Body: com.github.jcable.InputObject@7a34b7b8]
END RequestId: cd6cd879-22b9-4ac8-ac5b-751d1cdd000f
REPORT RequestId: cd6cd879-22b9-4ac8-ac5b-751d1cdd000f	Duration: 2.67 ms	Billed Duration: 3 ms	Memory Size: 256 MB	Max Memory Used: 144 MB	
{"result":"hello Bill","requestId":"aws-request-1"}
