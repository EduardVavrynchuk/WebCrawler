package com.webcrawler.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class UrlQueueGenerator {

    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private static final Log LOGGER = LogFactory.getLog(UrlQueueGenerator.class);

    private static Integer getUrlsFromPage(String startUrl, Collection<String> mainQueue, Collection<String> subQueue, Integer maxUrls) {
        try {
            Connection connection = Jsoup.connect(startUrl).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            if (connection.response().statusCode() == 200) {
                LOGGER.info("**Visiting** Received web page at " + startUrl);
            }
            if (!connection.response().contentType().contains("text/html")) {
                LOGGER.error("**Failure** Retrieved something other than HTML");
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            LOGGER.info("Found (" + linksOnPage.size() + ") links");
            for (Element link : linksOnPage) {
                String url = link.absUrl("href");
                if (!mainQueue.contains(url) && !subQueue.contains(url)) {
                    if (mainQueue.size() < maxUrls) {
                        subQueue.add(url);
                        mainQueue.add(url);
                    }
                }
            }
            System.out.println("Main queue = " + mainQueue.size());
            System.out.println("Sub queue = " + subQueue.size());
            return linksOnPage.size();
        } catch (IOException ioe) {
            LOGGER.warn("HTTP request is not successful");
            return 0;
        }
    }

    /**
     * creating queue of links by algorithm of bypass in width
     *
     * @param startUrl
     * @param maxUrls
     * @return - queue of links
     */
    public static Collection<String> generateUrlQueue(String startUrl, Integer maxUrls) {
        LinkedList<String> listOfUrls = new LinkedList<>(Collections.singletonList(startUrl));
        LinkedList<String> queue = new LinkedList<>(Collections.singletonList(startUrl));

        while (listOfUrls.size() < maxUrls || queue.isEmpty()) {
            getUrlsFromPage(queue.pollFirst(), listOfUrls, queue, maxUrls);
        }

        return listOfUrls;
    }
}
