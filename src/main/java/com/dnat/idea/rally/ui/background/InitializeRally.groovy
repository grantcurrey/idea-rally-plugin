package com.dnat.idea.rally.ui.background

import com.dnat.idea.rally.connector.RallySession
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull

class InitializeRally extends Task.Backgroundable {

    public InitializeRally(Project project) {
        super(project, "Loading Jenkins Jobs", true);
    }

    public void run(@NotNull ProgressIndicator progressIndicator) {
        def project = getProject()
        RallySession.getInstance(project)
    }
}