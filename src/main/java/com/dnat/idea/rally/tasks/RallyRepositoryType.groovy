package com.dnat.idea.rally.tasks

import com.intellij.tasks.TaskRepository
import com.intellij.tasks.impl.BaseRepositoryType

import icons.TasksIcons

import javax.swing.Icon

class RallyRepositoryType extends BaseRepositoryType<RallyRepository> {

    @Override
    String getName() {
        return "Rally"
    }

    @Override
    Icon getIcon() {
        return TasksIcons.Github;
    }

    @Override
    TaskRepository createRepository() {
        return new RallyRepository(this)
    }

    @Override
    Class<RallyRepository> getRepositoryClass() {
        return RallyRepository.class
    }
}
