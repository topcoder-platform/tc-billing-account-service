package com.appirio.service.billingaccount.api;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
public class PublisherSQS implements PublisherConsumedAmount
{
    private static final Logger logger = LoggerFactory.getLogger(PublisherSQS.class);
    public void publish(String msg)
    {
        try
        {
            String urlSQS = System.getenv("AWS_SQS_URL");
            AmazonSQS sqsClient = AmazonSQSAsyncClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();

            SendMessageRequest messsageRequest = new SendMessageRequest()
                .withQueueUrl(urlSQS==null?"https://sqs.us-east-1.amazonaws.com/811668436784/SQSTestQUEUESaroj":urlSQS)
                .withMessageBody(msg);
            sqsClient.sendMessage(messsageRequest);
        }
        catch(Exception e)
        {
            logger.debug("Unable to send message to SQS "+msg);
            //TODO : Retry mechanism
        }
    }
}
