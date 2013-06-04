package com.dnat.idea.rally.tasks

import com.dnat.idea.rally.connector.entity.Story
import com.intellij.tasks.Comment
import com.intellij.tasks.Task
import com.intellij.tasks.TaskType

import javax.swing.Icon

class RallyTask extends Task {
    private Story story
    private String url

    public RallyTask(Story story, String url){
        this.story = story
        this.url = url
    }

    @Override
    String getId() {
        return story.getFormattedId()
    }

    @Override
    String getSummary() {
        return "${story.formattedId} - ${story.getName()}"
    }

    @Override
    String getDescription() {
        return story.getName()
    }

    @Override
    Comment[] getComments() {
        return new Comment[0]  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Icon getIcon() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    TaskType getType() {
        return TaskType.FEATURE
    }

    @Override
    Date getUpdated() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Date getCreated() {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean isClosed() {
        return story.scheduleState == "Accepted"
    }

    @Override
    boolean isIssue() {
        return false
    }

    @Override
    String getIssueUrl() {
        return url
    }
}
