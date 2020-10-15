package com.webcrawler.crawl;

import java.util.Collection;
import java.util.concurrent.Callable;

public abstract class ICrawler implements Callable {

	public abstract int total();

	public abstract boolean isRunning();

	public abstract String getId();

	public abstract String getSearchWord();

	public abstract int getMaxThreads();

	public abstract int progress();

	public abstract Collection getCrawlResult();

}
