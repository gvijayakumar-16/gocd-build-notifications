package com.vijayakumar.gocd.buildnotification.executors;

import com.vijayakumar.gocd.buildnotification.HttpClient;
import com.vijayakumar.gocd.buildnotification.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class WebhookRequestExecutor implements RequestExecutor {

    private final List<String> endpoints;
    private final String requestBody;
    private final HttpClient sender;
    private static final Logger LOGGER = Logger.getLoggerFor(WebhookRequestExecutor.class);

    public WebhookRequestExecutor(HttpClient sender, List<String> endpoints, String requestBody) {
        this.endpoints = endpoints;
        this.requestBody = requestBody;
        this.sender = sender;
    }

    @Override
    public GoPluginApiResponse execute() {
        JSONArray exceptions = new JSONArray();
        for (String endpoint : endpoints) {
            LOGGER.info(String.format("Sending request to %s ", endpoint));
            try {
                sender.postToEndpoint(endpoint, requestBody);
            } catch (Exception e) {
                LOGGER.info(String.format("Failed reqeust: %s ", e.toString()));
                exceptions.put(e.toString());
            }
        }
        JSONObject res = new JSONObject();
        if (exceptions.isEmpty()) {
            res.put("status", "success");
        } else {
            res.put("status", "failure");
            res.put("messages", exceptions);
        }
        return new DefaultGoPluginApiResponse(200, res.toString());
    }
}
