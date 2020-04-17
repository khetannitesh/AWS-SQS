package com.paytm.sqs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

@Controller
public class SQSController {

	private static final Logger log = LoggerFactory.getLogger(SQSController.class);

	@Value("${sqs.url}")
	private String sqsURL;
	@Value("${amazonProperties.accessKey}")
	private String accessKey;
	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	@RequestMapping(value = "/sendMsgToQueue", method = RequestMethod.POST)
	public @ResponseBody void write(@RequestBody String notificationData) {
		try {
			AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
			final AmazonSQS sqs = AmazonSQSClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.AP_SOUTH_1)
					.build();

			log.info("Sending a message to Queue.\n");
			sqs.sendMessage(new SendMessageRequest(sqsURL, notificationData).withMessageGroupId("TestUserGroup"));
			log.info("Message Sent.\n");

		} catch (final AmazonServiceException ase) {
			log.error("AmazonServiceException, request reached Amazon SQS, but was "
					+ "rejected with an error");
			log.error("Error Message:    " + ase.getMessage());
			log.error("HTTP Status Code: " + ase.getStatusCode());
			log.error("AWS Error Code:   " + ase.getErrorCode());
			log.error("Error Type:       " + ase.getErrorType());
			log.error("Request ID:       " + ase.getRequestId());
		} catch (final AmazonClientException ace) {
			log.error("AmazonClientException the client encountered a serious internal while trying to reach Amazon SQS");
			log.error("Error Message: " + ace.getMessage());
		}
	}
}
