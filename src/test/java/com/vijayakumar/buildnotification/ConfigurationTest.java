package com.vijayakumar.gocd.buildnotification;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import java.io.File;
import java.util.List;

import com.vijayakumar.gocd.buildnotification.Configuration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {

    @Test
    public void shouldReadConfigFile() throws Exception {
        File configFilePath = new File(SystemUtils.getUserDir(), "src/test/resources/notification.properties");
        Configuration configuration = Configuration.loadConfiguration(configFilePath);

        List<String> endpoints = configuration.getStageEndpoints();
        assertThat(endpoints.size(), is(2));
        assertThat(endpoints, containsInAnyOrder("http://webhook/stage/1", "http://webhook/stage/2"));

        String host = configuration.getGoAPIServerHost();
        assertThat(host, is("https://host:3899"));

        String login = configuration.getGoLogin();
        assertThat(login, is("go"));

        String password = configuration.getGoPassword();
        assertThat(password, is("password"));
    }
}
