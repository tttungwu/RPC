package cn.edu.xmu.filter.client;

import cn.edu.xmu.filter.Filter;
import cn.edu.xmu.filter.FilterData;
import cn.edu.xmu.filter.FilterResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RateLimitingFilter implements Filter {

    private final int MAX_REQUESTS_PER_SECOND = 10;

    private int requestCount = 0;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RateLimitingFilter() {
        // 每秒复位请求计数
        scheduler.scheduleAtFixedRate(() -> requestCount = 0, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public synchronized FilterResponse doFilter(FilterData data) {
        if (requestCount >= MAX_REQUESTS_PER_SECOND) {
            return new FilterResponse(new Exception("Rate limit exceeded"));
        }
        requestCount ++;
        return new FilterResponse(true);
    }

    // 确保在应用关闭时关闭调度器
    public void shutdown() {
        scheduler.shutdown();
    }
}
