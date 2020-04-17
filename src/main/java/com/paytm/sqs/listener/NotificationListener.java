package com.paytm.sqs.listener;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

@Component
public class NotificationListener {

	private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
	private AmazonSQS sqs;
	@Value("${sqs.url}")
	private String sqsURL;
	@Value("${amazonProperties.accessKey}")
	private String accessKey;
	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.sqs = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.AP_SOUTH_1).build();
	}

	@Scheduled(fixedRate = 3000, initialDelay = 60000)
	@Async("taskExecutor")
	public void getMessage() {
		while (true) {
			log.info("Receiving messages from MyQueue.\n");
			final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsURL)
					.withMaxNumberOfMessages(1).withWaitTimeSeconds(3);
			final List<com.amazonaws.services.sqs.model.Message> messages = sqs.receiveMessage(receiveMessageRequest)
					.getMessages();
			for (final com.amazonaws.services.sqs.model.Message message : messages) {
				log.info("Message");
				log.info("MessageId:     " + message.getMessageId());
				log.info("ReceiptHandle: " + message.getReceiptHandle());
				log.info("Body:          " + message.getBody());

				if (!"".equals(message.getBody())) {
					try {
						Thread.sleep(2000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					log.info("Processing Complete - Deleting message.\n");
					final String messageReceiptHandle = messages.get(0).getReceiptHandle();
					sqs.deleteMessage(new DeleteMessageRequest(sqsURL, messageReceiptHandle));

				}

			}
		}
	}
}
