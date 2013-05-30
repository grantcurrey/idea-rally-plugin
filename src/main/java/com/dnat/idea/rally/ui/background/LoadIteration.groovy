package com.dnat.idea.rally.ui.background

import com.dnat.idea.rally.connector.RallySession
import com.dnat.idea.rally.ui.GuiUtil
import com.dnat.idea.rally.ui.RallyPanel
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class LoadIteration extends Task.Backgroundable {

    RallyPanel rallyPanel

    public LoadIteration(@Nullable Project project, RallyPanel rallyPanel) {
        super(project, "Loading Jenkins Jobs", true);
        this.rallyPanel = rallyPanel
    }

    public void run(@NotNull ProgressIndicator progressIndicator) {
        GuiUtil.runInSwingThread(new Runnable() {
            public void run() {
                rallyPanel.selectIteration(RallySession.instance.selectedIteration, RallySession.instance.getStoriesForSelectedIteration())
            }
        });
    }
}