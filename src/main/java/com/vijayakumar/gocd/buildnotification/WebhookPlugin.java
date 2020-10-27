package com.vijayakumar.gocd.buildnotification;

import com.vijayakumar.gocd.buildnotification.executors.GetPluginConfigurationExecutor;
import com.vijayakumar.gocd.buildnotification.jsonapi.*;
import com.vijayakumar.gocd.buildnotification.executors.GetViewRequestExecutor;
import com.vijayakumar.gocd.buildnotification.executors.NotificationInterestedInExecutor;
import com.vijayakumar.gocd.buildnotification.executors.ValidateConfigurationExecutor;
import com.vijayakumar.gocd.buildnotification.executors.WebhookRequestExecutor;
import com.vijayakumar.gocd.buildnotification.jsonapi.Stage;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.logging.Logger;

import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import static com.vijayakumar.gocd.buildnotification.Constants.PLUGIN_IDENTIFIER;

@Extension
public class WebhookPlugin implements GoPlugin {

    private Logger LOGGER = Logger.getLoggerFor(WebhookPlugin.class);

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor accessor) {
        // ignored
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest request) throws UnhandledRequestTypeException {
        try {
            return executorForRequest(request).execute();
        } catch (UnhandledRequestTypeException e) {
            throw e; // no need to wrap in a runtime exception
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return PLUGIN_IDENTIFIER;
    }

    private RequestExecutor executorForRequest(GoPluginApiRequest request) throws Exception {
        switch (PluginRequest.fromString(request.requestName())) {
            case PLUGIN_SETTINGS_GET_VIEW:
                return new GetViewRequestExecutor();
            case REQUEST_NOTIFICATIONS_INTERESTED_IN:
                return new NotificationInterestedInExecutor();
            case REQUEST_STAGE_STATUS:
                return getStageStatusWebhook(request);
            case PLUGIN_SETTINGS_GET_CONFIGURATION:
                return new GetPluginConfigurationExecutor();
            case PLUGIN_SETTINGS_VALIDATE_CONFIGURATION:
                return new ValidateConfigurationExecutor();
            default:
                throw new UnhandledRequestTypeException(request.requestName());
        }
    }

    private GoNotificationMessage parseNotificationMessage(GoPluginApiRequest goPluginApiRequest) {
        return new GsonBuilder().create().fromJson(goPluginApiRequest.requestBody(), GoNotificationMessage.class);
    }

    private RequestExecutor getStageStatusWebhook(GoPluginApiRequest request) throws Exception {
        Configuration config = Configuration.getCurrent();

        GoNotificationMessage message = parseNotificationMessage(request);
        String additionalDetails = "";
        try {
            int DEFAULT_MAX_CHANGES_PER_MATERIAL_IN_SLACK = 5;
            Pipeline details = message.fetchDetails();
            Stage stage = pickCurrentStage(details.stages, message);
            additionalDetails = details.toString();
        } catch (Exception e) {
            LOGGER.info(String.format("Couldn't fetch build details %s", e.toString()));
        }
        
        String jsonDataString = request.requestBody();
        JSONObject mainObject = new JSONObject(jsonDataString);
        mainObject.accumulate("AdditionalDetails", additionalDetails);
        return new WebhookRequestExecutor(config.getClient(), config.getStageEndpoints(), mainObject.toString());
    }

    private Stage pickCurrentStage(Stage[] stages, GoNotificationMessage message) {
        for (Stage stage : stages) {
            if (message.getStageName().equals(stage.name)) {
                return stage;
            }
        }

        throw new IllegalArgumentException("The list of stages from the pipeline (" + message.getPipelineName() + ") doesn't have the active stage (" + message.getStageName() + ") for which we got the notification.");
    }
}
