package com.dnat.idea.rally.ui

import com.dnat.idea.rally.ui.action.OpenPluginSettingsAction
import com.dnat.idea.rally.ui.action.SelectViewAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class RallyToolWindow implements ToolWindowFactory {

    private RallyPanel rallyPanel

    void createToolWindowContent(Project project, ToolWindow toolWindow) {
        rallyPanel = new RallyPanel()

        def component = toolWindow.getComponent()

        def parent = new SimpleToolWindowPanel(true)
        parent.add(rallyPanel.mainPanel)
        GuiUtil.installActionGroupInToolBar(createToolbar(), parent, "rallyActions");
        component.parent.add(parent)
    }

    private def createToolbar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup("RallyToolbarGroup", false);
        actionGroup.add(new SelectViewAction())
        actionGroup.addSeparator();
        actionGroup.add(new OpenPluginSettingsAction());

       return actionGroup
    }
}
