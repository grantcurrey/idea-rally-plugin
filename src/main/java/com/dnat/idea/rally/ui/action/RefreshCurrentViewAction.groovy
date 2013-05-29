package com.dnat.idea.rally.ui.action

import com.dnat.idea.rally.connector.RallySession
import com.dnat.idea.rally.ui.GuiUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class RefreshCurrentViewAction extends AnAction implements DumbAware {
    public RefreshCurrentViewAction() {
        super("Refresh Current View", "Refresh the current selected rally view", GuiUtil.isUnderDarcula() ? GuiUtil.loadIcon("refresh_dark.png") : GuiUtil.loadIcon("refresh.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        GuiUtil.runInSwingThread({
            RallySession.instance.refresh()

        })
    }
}
