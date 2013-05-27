package com.dnat.idea.rally.ui.action

import com.dnat.idea.rally.ui.GuiUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class OpenPluginSettingsAction extends AnAction implements DumbAware {

    public OpenPluginSettingsAction() {
        super("Rally Settings", "Edit the Rally settings for the current project", GuiUtil.isUnderDarcula() ? GuiUtil.loadIcon("settings_dark.png") : GuiUtil.loadIcon("settings.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        print "here"
    }

}
