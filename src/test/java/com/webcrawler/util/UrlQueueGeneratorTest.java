package com.webcrawler.util;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UrlQueueGeneratorTest {

	@Test
	void generateUrlQueue() {
		String startUrl = "https://smallbusiness.chron.com/";
		Integer maxUrl = 500;
		Collection<String> urls = UrlQueueGenerator.generateUrlQueue(startUrl, maxUrl);

		assertEquals(maxUrl, urls.size());
	}
}