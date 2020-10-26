package com.vijayakumar.gocd.buildnotification;

import com.thoughtworks.go.plugin.api.logging.Logger;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.unmodifiableList;

public class Configuration {

    private static final Logger LOGGER = Logger.getLoggerFor(Configuration.class);

    private static final String BUILD_NOTIFICATION_CONFIGURATION = "BUILD_NOTIFICATION_CONFIGURATION";

    private static File findConfigFile() {
        String configFilePath = System.getenv(BUILD_NOTIFICATION_CONFIGURATION);
        if (StringUtils.isNotEmpty(configFilePath)) {
            File pluginConfig = new File(configFilePath);
            if (pluginConfig.exists()) {
                LOGGER.info(String.format("Configuration file found using BUILD_NOTIFICATION_CONFIGURATION at %s", pluginConfig.getAbsolutePath()));
                return pluginConfig;
            }
        }
        throw new RuntimeException("Unable to find configuration file in location set by BUILD_NOTIFICATION_CONFIGURATION. Please make sure you've set it up right.");
    }

    public static Configuration loadConfiguration(File pluginConfig) throws Exception {
        Properties props = new Properties();
        FileInputStream input = new FileInputStream(pluginConfig);
        props.load(input);

        boolean trustAllHttps = BooleanUtils.toBoolean(props.getProperty("trust.all.https", "false"));
        String goLogin = props.getProperty("gocd.username");
        String goPassword = props.getProperty("gocd.password");
        String goHost = props.getProperty("gocd.host");
        LOGGER.info(String.format("Host %s ", goHost));
        HttpClient client = new HttpClient(trustAllHttps);

        List<String> stageEndpoints = new ArrayList<>();
        Enumeration<?> names = props.propertyNames();
        
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            if (name.startsWith("stage.status.endpoint.")) {
                stageEndpoints.add(props.getProperty(name));
            }
        }
        return new Configuration(client, stageEndpoints, goLogin, goPassword, goHost);
    }

    private static Configuration current;
    private static long lastModified;
    private static File configFile;

    public synchronized static Configuration getCurrent() throws Exception {
        if (current != null && lastModified == configFile.lastModified()) {
            return current;
        }
        if (configFile == null) {
            configFile = findConfigFile();
        }
        lastModified = configFile.lastModified();
        current = loadConfiguration(configFile);
        return current;
    }

    private final HttpClient client;
    private final List<String> stageEndpoints;
    private final String goLogin;
    private final String goPassword;
    private final String goHost;

    private Configuration(HttpClient client, List<String> stageEndpoints, String goLogin, String goPassword, String goHost) {
        this.stageEndpoints = unmodifiableList(stageEndpoints);
        this.client = client;
        this.goLogin = goLogin;
        this.goPassword = goPassword;
        this.goHost = goHost;
    }

    public HttpClient getClient() {
        return client;
    }

    public List<String> getStageEndpoints() {
        return stageEndpoints;
    }

    public String getGoLogin() {
        return goLogin;
    }

    public String getGoPassword() {
        return goPassword;
    }

    public String getGoAPIServerHost() {
        return goHost;
    }
}
