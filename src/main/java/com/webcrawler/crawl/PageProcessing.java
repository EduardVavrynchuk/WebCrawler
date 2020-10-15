package com.webcrawler.crawl;

import com.webcrawler.util.UrlQueueGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class PageProcessing implements Callable {

	private static final Log LOGGER = LogFactory.getLog(PageProcessing.class);
	public String link;
	public transient String searchWord;
	public int amountWords = 0;
	public PageProcessingStatus status;
	public String msg;
	public PageProcessing(String url, String searchWord) {
		this.link = url;
		this.searchWord = searchWord;
		this.status = PageProcessingStatus.QUEUE;
	}

	@Override
	public Object call() {
		try {
			status = PageProcessingStatus.PROCESSING;
			Connection connection = Jsoup.connect(link).userAgent(UrlQueueGenerator.USER_AGENT);
			Document document = connection.get();
			if (document == null) {
				status = PageProcessingStatus.ERROR;
				msg = "Page is empty";
				LOGGER.warn("Page is empty: " + link);
				return this;
			}

			Scanner scanner = new Scanner(document.body().text());
			String[] words = splitSearchWord();
			while (scanner.hasNextLine()) {
				String nextToken = scanner.next();
				for (String word : words) {
					if (nextToken.equalsIgnoreCase(word))
						amountWords++;
				}
			}
			status = PageProcessingStatus.FINISHED;
		} catch (IOException e) {
			status = PageProcessingStatus.ERROR;
			msg = "Error while page loading: " + e.getMessage();
			LOGGER.warn("Error with loading page: " + link, e);
		}

		return this;
	}

	private String[] splitSearchWord() {
		String[] words;
		if (searchWord.contains(" ")) {
			words = searchWord.split(" ");
		} else {
			words = new String[]{searchWord};
		}

		return words;
	}

	public enum PageProcessingStatus {
		QUEUE,
		PROCESSING,
		FINISHED,
		ERROR
	}

}
