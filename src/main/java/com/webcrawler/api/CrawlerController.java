package com.webcrawler.api;

import com.google.gson.Gson;
import com.webcrawler.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
public class CrawlerController {

    @Autowired
    private CrawlerService crawlerService;

    private Gson gson = new Gson();

    @PostMapping(path = "/start", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> startCrawler(@RequestParam("url") String url,
                                               @RequestParam("maxThreads") Integer maxThreads,
                                               @RequestParam("searchText") String searchText,
                                               @RequestParam("maxUrls") Integer maxUrls) {
        if (crawlerService.startCrawler(url, maxThreads, searchText, maxUrls)) {
            return ResponseEntity.ok("Crawler instance was created!");
        } else {
            return ResponseEntity.ok("Error while crawler instance creating");
        }
    }

    @GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getCrawlerInfo() {
        return gson.toJson(crawlerService.getCrawlerInfo());
    }

}
