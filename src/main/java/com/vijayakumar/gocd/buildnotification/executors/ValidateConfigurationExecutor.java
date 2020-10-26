package com.vijayakumar.gocd.buildnotification.executors;

import com.vijayakumar.gocd.buildnotification.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public class ValidateConfigurationExecutor implements RequestExecutor {

    public GoPluginApiResponse execute() {
        return DefaultGoPluginApiResponse.success("[]");
    }
}
