package com.webcrawler.crawl;

import com.webcrawler.service.CrawlerService;
import com.webcrawler.util.UrlQueueGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Crawler extends ICrawler {

	private static final Log LOGGER = LogFactory.getLog(CrawlerService.class);

	private String startUrl;
	private Integer maxThreads;
	private String searchWord;
	private Integer maxUrls;
	private ExecutorService executorService;

	private List<PageProcessing> pages = new LinkedList<>();
	private List<Future<?>> futures = new LinkedList<>();

	public Crawler(String url, Integer maxThreads, String searchWord, Integer maxUrls) {
		LOGGER.info("Creating crawler instance for start url: " + url);
		this.startUrl = url;
		this.searchWord = searchWord;
		this.maxUrls = maxUrls;
		this.maxThreads = maxThreads;

		executorService = Executors.newFixedThreadPool(maxThreads);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object call() {
		Collection<String> pagesForVisit = UrlQueueGenerator.generateUrlQueue(startUrl, maxUrls);
		LOGGER.info("Creating executor queue");
		for (String link : pagesForVisit) {
			PageProcessing pageProcessing = new PageProcessing(link, searchWord);
			pages.add(pageProcessing);
			futures.add(executorService.submit(pageProcessing));
		}

		return this;
	}

	@Override
	public String getSearchWord() {
		return searchWord;
	}

	@Override
	public int progress() {
		int amountFinished = 0;
		for (Future<?> future : futures) {
			if (future.isDone()) {
				amountFinished++;
			}
		}
		return amountFinished;
	}

	@Override
	public int total() {
		return maxUrls;
	}

	@Override
	public int getMaxThreads() {
		return maxThreads;
	}

	@Override
	public boolean isRunning() {
		if (futures.isEmpty()) {
			return true;
		}

		for (Future<?> future : futures)
			if (!future.isDone())
				return true;

		return false;
	}

	@Override
	public String getId() {
		return "Crawler for " + startUrl;
	}

	@Override
	public List<PageProcessing> getCrawlResult() {
		return pages;
	}

}
