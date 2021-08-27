/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.flpt.pressure.engine.health;

import io.shulie.flpt.pressure.engine.api.plugin.PressureContext;
import io.shulie.flpt.pressure.engine.util.JsonUtils;
import io.shulie.flpt.pressure.engine.util.StringUtils;
import io.shulie.flpt.pressure.engine.util.SystemResourceUtil;
import io.shulie.flpt.pressure.engine.util.TryUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: io.shulie.flpt.pressure.engine.health
 * @Date 2020-05-09 14:06
 */
public class HttpServerInfoReport {

    private static Logger logger = LoggerFactory.getLogger(HttpServerInfoReport.class);

    private CloseableHttpAsyncClient httpClient;
    private HttpPost httpRequest;
    private URL url;
    private String ip = SystemResourceUtil.getLocalInetAddress();
    private Future<HttpResponse> lastRequest;

    public void start(PressureContext context) throws Exception {
        IOReactorConfig ioReactorConfig = IOReactorConfig
                .custom()
                .setIoThreadCount(1)
                .setConnectTimeout(1000)
                .setSoTimeout(3000)
                .build();

        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);

        PoolingNHttpClientConnectionManager connManager =
                new PoolingNHttpClientConnectionManager(ioReactor);

        httpClient = HttpAsyncClientBuilder.create()
                .setConnectionManager(connManager)
                .setMaxConnPerRoute(2)
                .setMaxConnTotal(2)
                .setUserAgent("Pressure-Engine" + "1.0")
                .disableCookieManagement()
                .disableConnectionState()
                .build();

        String consoleUrl = TryUtils.tryOperation(() -> String.valueOf(context.getTaskParams().get("consoleUrl")));

        if(StringUtils.isNotBlank(consoleUrl)) {
            consoleUrl = consoleUrl + "/api/collector/system/receive?ip=" + ip;
        }

        url = new URL(consoleUrl);
        httpRequest = createRequest(url);
        httpClient.start();
    }

    private HttpPost createRequest(URL url) throws URISyntaxException {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(1000)
                .setSocketTimeout(3000)
                .setConnectionRequestTimeout(100)
                .build();

        HttpPost currentHttpRequest = new HttpPost(url.toURI());
        currentHttpRequest.setConfig(defaultRequestConfig);

        logger.debug("Created Collection centre MetricsSender with url: {}", url);
        return currentHttpRequest;
    }

    public void sendHttp(Map<String, Object> pamrasMap) throws Exception {
        if (null == httpRequest) {
            httpRequest = createRequest(url);
        }

        try{
            String body = JsonUtils.obj2Json(pamrasMap);
            logger.info("server info : {}" , body);
            httpRequest.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
            lastRequest = httpClient.execute(httpRequest, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(final HttpResponse response) {
                    int code = response.getStatusLine().getStatusCode();

                    if (code >= 200 && code <=399) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Success, number of server info written: {}", pamrasMap.size());
                        }
                    } else {
                        logger.error("Error writing server info to Collection centre Url: {}, responseCode: {}, responseBody: {}", url, code, getBody(response));
                    }
                }

                @Override
                public void failed(final Exception ex) {
                    logger.error("failed to send http url:{}.", url, ex);
                }

                @Override
                public void cancelled() {
                    logger.warn("Request to Collection centre server was cancelled");
                }
            });
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param response HttpResponse
     * @return String entity Body if any
     */
    private static String getBody(final HttpResponse response) {
        String body = "";
        try {
            if (response != null && response.getEntity() != null) {
                body = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) { // NOSONAR
            // NOOP
        }
        return body;
    }

    public void destroy() {
        // Give some time to send last metrics before shutting down
        logger.info("Destroying ");
        try {
            lastRequest.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("Error waiting for last request to be send to http", e);
        }
        if (httpRequest != null) {
            httpRequest.abort();
        }
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
