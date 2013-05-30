package com.dnat.idea.rally

import com.dnat.idea.rally.connector.RallySession
import com.dnat.idea.rally.connector.RallySettings
import com.dnat.idea.rally.ui.GuiUtil
import com.dnat.idea.rally.ui.RallySettingsPanel
import com.dnat.idea.rally.ui.background.InitializeRally
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project

import javax.swing.*

class RallyComponent implements ProjectComponent, Configurable {

    Project project

    RallySettings rallySettings

    RallySettingsPanel rallySettingsPanel

    public RallyComponent(Project project) {
        this.project = project
        this.rallySettings = RallySettings.getSafeInstance(this.project)

        new InitializeRally(project).queue()

        rallySettingsPanel = new RallySettingsPanel()
    }

    void projectOpened() {

    }

    void projectClosed() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    void initComponent() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    void disposeComponent() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    String getComponentName() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    String getDisplayName() {
        "Rally Settings"
    }

    String getHelpTopic() {
        return null
    }

    JComponent createComponent() {
        return rallySettingsPanel.mainPanel
    }

    boolean isModified() {
        return true
    }

    void apply() throws ConfigurationException {
        rallySettingsPanel.applyConfigurationData(this.rallySettings)
        GuiUtil.runInSwingThread({ RallySession.instance.initialise(this.rallySettings) })
    }

    void reset() {
        rallySettingsPanel.loadConfigurationData(this.rallySettings)
    }

    void disposeUIResources() {

    }
}
