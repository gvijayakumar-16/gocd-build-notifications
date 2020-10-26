package com.vijayakumar.gocd.buildnotification.executors;

import com.vijayakumar.gocd.buildnotification.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class GetPluginConfigurationExecutor implements RequestExecutor {

    public GoPluginApiResponse execute() {
        return new DefaultGoPluginApiResponse(200, "{}");
    }
}
