package com.vijayakumar.gocd.buildnotification;

import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

public interface RequestExecutor {

    GoPluginApiResponse execute() throws Exception;
}
