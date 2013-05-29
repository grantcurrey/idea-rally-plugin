package com.dnat.idea.rally.connector;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

@State(
        name = "RallySettings",
        storages = {
                @Storage(id = "RallySettings", file = "$PROJECT_FILE$"),
                @Storage(file = "$PROJECT_CONFIG_DIR$/rallySettings.xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class RallySettings implements PersistentStateComponent<RallySettings> {

    String username = "";
    String password = "";
    String url = "https://www.rallydev.com";

    public static RallySettings getSafeInstance(Project project) {
        RallySettings settings = ServiceManager.getService(project, RallySettings.class);
        return settings != null ? settings : new RallySettings();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RallySettings getState() {
        return this;
    }

    public void loadState(RallySettings rallySettings) {
        XmlSerializerUtil.copyBean(rallySettings, this);
    }
}
