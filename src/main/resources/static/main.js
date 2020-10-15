var MAIN_VIEW = function () {
    return {
        onReady: function () {
            $("#start-crawler-btn").click(function () {
                var url = $("#start-url").val();
                var maxThreads = $("#max-threads").val();
                var searchWord = $("#search-word").val();
                var maxUrls = $("#max-urls").val();

                $.post("api/start?url=" + url + "&maxThreads=" + maxThreads + "&searchText=" + searchWord + "&maxUrls=" + maxUrls, {})
                    .done(function (data) { $("#refresh-info-btn").click(); })
                    .fail(function (xhr, status, error) { console.log(data);
                    });
            });
            
            $("#refresh-info-btn").click(function () {
                getJsonAction("/api/info", function (data) {
                    generateCrawlerResponse(data);
                })
            });

        }
    };

    function generateCrawlerResponse(data) {
        var items = $("#crawler-list");
        items.empty();
        let templateItem = $("#crawler-list-item");
        if (JSON.stringify(data) !== '{}') {
            data.forEach(function (crawl) {
                var it = templateItem.clone();
                it.find("[did='crawler-id']").html(crawl.crawlerId);
                it.find("[did='status-crawler']").html(crawl.isRunning===true?"Processing":"Finished");
                it.find("[did='max-threads']").html(crawl.maxThreads);
                it.find("[did='search-word']").html(crawl.searchWord);
                it.find("[did='processed-urls']").html(crawl.progress);

                console.log(crawl.crawlerId);
                var arr = crawl.listLinksResult;
                var jsonString = "";
                it.find("[did='scanned-urls']").html(arr.length + "/" +crawl.total);
                arr.forEach(function (ar) {
                    let str = "";
                    if(ar.status === "PROCESSING" || ar.status === "QUEUE") {
                        str = ar.status
                    } else if (ar.status === "ERROR") {
                        str = ar.status + " -> " + ar.msg;
                    } else if (ar.status === "FINISHED") {
                        str = ar.status + " : ";
                        if (ar.amountWords === 0) {
                            str += "Words not found";
                        } else {
                            str +="Number of words found: " + ar.amountWords;
                        }
                    }
                    jsonString += '<a href="'+ar.link+'">'+ar.link+'</a>' + " : " + str + "\n";
                });
                it.find("[did='crawl-result']").html(jsonString);
                items.append(it);
            });
        }
    }
}();

function getJsonAction(url, functionDone, obj) {
    return $.getJSON(url, obj ? obj : {}, function (data) {
        functionDone(data);
    });
}

$(document).ready(function () {
    MAIN_VIEW.onReady();
});