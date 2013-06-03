package com.dnat.idea.rally.ui.background

import com.dnat.idea.rally.connector.RallySession
import com.dnat.idea.rally.ui.GuiUtil
import com.dnat.idea.rally.ui.RallyPanel
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

class LoadIteration extends Task.Backgroundable {

    RallyPanel rallyPanel

    public LoadIteration(Project project, RallyPanel rallyPanel) {
        super(project, "Loading Jenkins Jobs", true);
        this.rallyPanel = rallyPanel
    }

    public void run(@NotNull ProgressIndicator progressIndicator) {
        def project = getProject()
        GuiUtil.runInSwingThread(new Runnable() {
            public void run() {
                def selectedIteration = RallySession.getInstance(project).selectedIteration
                def stories = RallySession.getInstance(project).getStoriesForSelectedIteration()
                rallyPanel.selectIteration(selectedIteration, stories)
            }
        });
    }
}