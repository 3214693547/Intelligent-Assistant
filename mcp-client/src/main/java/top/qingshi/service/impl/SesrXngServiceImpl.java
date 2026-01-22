package top.qingshi.service.impl;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.qingshi.bean.SearXNGResponse;
import top.qingshi.bean.SearchResult;
import top.qingshi.service.SesrXngService;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @ClassName SesrXngServiceImpl
 * @Version 1.0
 * @Description SesrXngServiceImpl
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class SesrXngServiceImpl implements SesrXngService
{

    @Value("${internet.websearch.searxng.url}")
    private String SEARXNG_URL;

    @Value("${internet.websearch.searxng.counts}")
    private Integer COUNTS;

    private final OkHttpClient okHttpClient;

    @Override
    public List<SearchResult> search(String query) {

        // 构建url，指定使用 Bing 搜索引擎（国内可用）
        HttpUrl url = HttpUrl.get(SEARXNG_URL)
                .newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("format", "json")
                .addQueryParameter("engines", "bing")  // 指定使用 Bing
                .build();

        log.info("搜索的url地址为：" + url.url());

        // 构建request，添加必要的请求头
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .build();

        // 发送请求
        try (Response response = okHttpClient.newCall(request).execute()) {

            // 判断请求是否成功还是失败
            if (!response.isSuccessful()) {
                log.error("请求失败: HTTP {} - {}", response.code(), response.message());
                throw new RuntimeException("请求失败: HTTP " + response.code());
            }

            // 获得响应的数据
            if (response.body() != null) {
                String responseBody = response.body().string();
                
                log.info("响应内容长度: {}", responseBody.length());
                log.info("响应内容: {}", responseBody);

                SearXNGResponse searXNGResponse = JSONUtil.toBean(responseBody, SearXNGResponse.class);

                return dealResults(searXNGResponse.getResults());
            }
            log.error("响应体为空，HTTP状态码: {}, 消息: {}", response.code(), response.message());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Collections.emptyList();
    }

    /**
     * @Description: 处理结果集，截取限制的个数
     * @Author 风间影月
     * @param results
     * @return List<SearchResult>
     */
    private List<SearchResult> dealResults(List<SearchResult> results) {

        return results.subList(0, Math.min(COUNTS, results.size()))
                        .parallelStream()
                        .sorted(Comparator.comparingDouble(SearchResult::getScore).reversed())
                        .limit(COUNTS).toList();
    }
}
