package com.vijayakumar.gocd.buildnotification.jsonapi;

import com.google.gson.JsonElement;
import com.thoughtworks.go.plugin.api.logging.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import com.vijayakumar.gocd.buildnotification.Configuration;

import org.apache.commons.lang3.StringUtils;

/**
 * Actual methods for contacting the remote server.
 */
public class Server {
    private Logger LOG = Logger.getLoggerFor(Server.class);

    private HttpConnectionUtil httpConnectionUtil;
    Configuration config;
    
    public Server() {
        try{
            config = Configuration.getCurrent();
        } catch(Exception e){
            throw new RuntimeException("Unable to load configuration file.");
        }
        httpConnectionUtil = new HttpConnectionUtil();
    }

    Server(HttpConnectionUtil httpConnectionUtil) {
        try{
            config = Configuration.getCurrent();
        } catch(Exception e){
            throw new RuntimeException("Unable to load configuration file.");
        }
        this.httpConnectionUtil = httpConnectionUtil;
    }

    JsonElement getUrl(URL url)
            throws IOException {
        URL normalizedUrl;
        LOG.info(String.format("URL %s ", url));
        try {
            normalizedUrl = url.toURI().normalize().toURL();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        LOG.info("Fetching " + normalizedUrl.toString());

        HttpURLConnection request = httpConnectionUtil.getConnection(normalizedUrl);
        request.setRequestProperty("Accept", "application/vnd.go.cd+json");
        request.setRequestProperty("User-Agent", "plugin/gocd.plugin");

        String authHeader = null;
        if (StringUtils.isNotEmpty(config.getGoLogin()) && StringUtils.isNotEmpty(config.getGoPassword())) {
            String userpass = config.getGoLogin() + ":" + config.getGoPassword();
            authHeader = "Basic " + DatatypeConverter.printBase64Binary(userpass.getBytes());
        }
        if (authHeader != null) {
            request.setRequestProperty("Authorization", authHeader);
        }

        request.connect();

        return httpConnectionUtil.responseToJson(request.getContent());
    }

    /**
     * Get the recent history of a pipeline.
     */
    public History getPipelineHistory(String pipelineName)
            throws MalformedURLException, IOException {
        LOG.info(String.format("Host at history %s ", config.getGoAPIServerHost()));
        URL url = new URL(String.format("%s/go/api/pipelines/%s/history",
                config.getGoAPIServerHost(), pipelineName));
        JsonElement json = getUrl(url);
        return httpConnectionUtil.convertResponse(json, History.class);
    }

    /**
     * Get a specific instance of a pipeline.
     */
    public Pipeline getPipelineInstance(String pipelineName, int pipelineCounter)
            throws MalformedURLException, IOException {
        LOG.info(String.format("Host at instance %s ", config.getGoAPIServerHost()));
        URL url = new URL(String.format("%s/go/api/pipelines/%s/%d",
                config.getGoAPIServerHost(), pipelineName, pipelineCounter));
        JsonElement json = getUrl(url);
        return httpConnectionUtil.convertResponse(json, Pipeline.class);
    }
}
