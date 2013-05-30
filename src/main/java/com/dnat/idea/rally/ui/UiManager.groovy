package com.dnat.idea.rally.ui

import com.dnat.idea.rally.ui.action.OpenPluginSettingsAction
import com.dnat.idea.rally.ui.action.RefreshCurrentViewAction
import com.dnat.idea.rally.ui.action.SelectIterationAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class UiManager implements ToolWindowFactory {
    RallyPanel rallyPanel

    Project project

    public UiManager(){
        this.rallyPanel = new RallyPanel()
    }

    void createToolWindowContent(Project project, ToolWindow toolWindow) {
        this.project = project

        def parent = new SimpleToolWindowPanel(true)
        parent.add(rallyPanel.mainPanel)
        GuiUtil.installActionGroupInToolBar(createToolbar(), parent, "rallyActions");
        toolWindow.getComponent().parent.add(parent)
    }

    private def createToolbar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup("RallyToolbarGroup", false);
        actionGroup.add(new SelectIterationAction(rallyPanel))
        actionGroup.add(new RefreshCurrentViewAction())
        actionGroup.addSeparator();
        actionGroup.add(new OpenPluginSettingsAction());

        return actionGroup
    }
}
