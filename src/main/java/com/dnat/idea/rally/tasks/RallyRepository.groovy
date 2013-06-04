package com.dnat.idea.rally.tasks

import com.dnat.idea.rally.connector.Rally
import com.dnat.idea.rally.connector.entity.Iteration
import com.dnat.idea.rally.connector.entity.Project
import com.dnat.idea.rally.connector.entity.Story
import com.intellij.tasks.Task
import com.intellij.tasks.impl.BaseRepository
import com.intellij.tasks.impl.BaseRepositoryImpl

class RallyRepository extends BaseRepositoryImpl {

    public RallyRepository() {
        super()
    }

    public RallyRepository(RallyRepositoryType type) {
        super(type)
    }

    public RallyRepository(RallyRepository repository){
        super(repository)
    }

    @Override
    Task[] getIssues(String s, int i, long l) throws Exception {
        Rally rally = new Rally(getUrl(), getUsername(), getPassword())
        Project project = rally.getCurrentProject()
        Iteration iteration = rally.getCurrentIterationForProject(project.objectId)
        List<Story> stories = rally.getStoriesForIteration(iteration.objectId)
        def tasks = []
        stories.each { Story story ->
            String url = "${getUrl()}/#/${project.objectId}/detail/userstory/${story.objectId}"
            tasks.add(new RallyTask(story, url))
        }
        return tasks
    }

    @Override
    Task findTask(String s) throws Exception {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    BaseRepository clone() {
        return new RallyRepository(this)
    }
}
