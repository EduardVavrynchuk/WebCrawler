package com.webcrawler.service;

import com.webcrawler.crawl.Crawler;
import com.webcrawler.crawl.ICrawler;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class CrawlerService {

	private LinkedList<ICrawler> crawlers = new LinkedList<>();
	private List<Future<?>> futures = new LinkedList<>();
	private ExecutorService service = Executors.newFixedThreadPool(1);

	public CrawlerService() {
	}

	@SuppressWarnings("unchecked")
	public boolean startCrawler(String url, Integer maxThreads, String searchWord, Integer maxUrls) {
		ICrawler crawler = new Crawler(url, maxThreads, searchWord, maxUrls);
		crawlers.add(crawler);
		futures.add(service.submit(crawler));
		return true;
	}

	public Collection<CrawlerStats> getCrawlerInfo() {
		LinkedList<CrawlerStats> crawlerStatsList = new LinkedList<>();
		Iterator<ICrawler> iterator = crawlers.descendingIterator();
		while (iterator.hasNext()) {
			crawlerStatsList.add(new CrawlerStats(iterator.next()));
		}

		return crawlerStatsList;
	}

	public static class CrawlerStats {
		public String crawlerId;
		public Integer total;
		public Integer maxThreads;
		public String searchWord;
		public boolean isRunning;
		public Integer progress;
		public Collection listLinksResult;

		public CrawlerStats(ICrawler iCrawler) {
			crawlerId = iCrawler.getId();
			total = iCrawler.total();
			maxThreads = iCrawler.getMaxThreads();
			searchWord = iCrawler.getSearchWord();
			isRunning = iCrawler.isRunning();
			progress = iCrawler.progress();
			listLinksResult = iCrawler.getCrawlResult();
		}
	}

}
