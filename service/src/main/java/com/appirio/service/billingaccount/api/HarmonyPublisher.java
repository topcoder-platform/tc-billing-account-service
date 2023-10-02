package com.appirio.service.billingaccount.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;

public class HarmonyPublisher implements PublisherConsumedAmount {

	private static final Logger logger = LoggerFactory.getLogger(HarmonyPublisher.class);

	private final AWSLambda awsLambda;

	public HarmonyPublisher() {
		awsLambda = AWSLambdaClientBuilder.standard().withCredentials(new DefaultAWSCredentialsProviderChain())
				.withRegion(Regions.US_EAST_1).build();
	}

	public void publish(String msg) {
		try {
			String functionName = System.getenv("PUBLISHER_LAMBDA_FUNCTION");

			awsLambda.invoke(new InvokeRequest().withInvocationType(InvocationType.Event).withFunctionName(functionName)
					.withPayload(msg));

		} catch (Exception e) {
			logger.error("Unable to send message to Harmony Publisher: " + msg, e);
		}
	}
}
