package com.vijayakumar.gocd.buildnotification.executors;

import com.vijayakumar.gocd.buildnotification.RequestExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.vijayakumar.gocd.buildnotification.PluginRequest.REQUEST_STAGE_STATUS;

public class NotificationInterestedInExecutor implements RequestExecutor {

    @Override
    public GoPluginApiResponse execute() {
        JSONArray notifications = new JSONArray();
        notifications.put(REQUEST_STAGE_STATUS.requestName());

        JSONObject obj = new JSONObject();
        obj.put("notifications", notifications);

        return new DefaultGoPluginApiResponse(200, obj.toString());
    }
}
