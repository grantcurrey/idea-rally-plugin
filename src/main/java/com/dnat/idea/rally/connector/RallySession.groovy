package com.dnat.idea.rally.connector

import com.dnat.idea.rally.connector.entity.Iteration
import com.dnat.idea.rally.connector.entity.Project
import com.dnat.idea.rally.connector.entity.User
import com.intellij.openapi.components.ServiceManager
import org.apache.log4j.Logger

class RallySession {

    private static final Logger LOG = Logger.getLogger(RallySession.class)

    public static RallySession getInstance(com.intellij.openapi.project.Project project) {
        RallySession singleton = ServiceManager.getService(project, RallySession.class)
        return singleton != null ? singleton : new RallySession(project)
    }

    User currentUser
    Project currentProject
    List<Iteration> futureIterations
    Iteration activeIteration

    Iteration selectedIteration

    def offline = true

    com.intellij.openapi.project.Project project
    Rally rally

    private RallySession(com.intellij.openapi.project.Project project) {
        this.project = project
        initialise()
    }

    def getCurrentUser(boolean refresh = false) {
        if (shouldRefresh(refresh, currentUser)) {
            currentUser = rally.userDetails
        }
        return currentUser
    }

    def getCurrentProject(boolean refresh = false) {
        if (shouldRefresh(refresh, currentProject)) {
            currentProject = rally.currentProject
        }
        return currentProject
    }

    def getFutureIterations(boolean refresh = false) {
        if (shouldRefresh(refresh, futureIterations)) {
            futureIterations = rally.getFutureIterationsForProject(currentProject.objectId)
        }
        return futureIterations
    }

    def getActiveIteration(boolean refresh = false) {
        if (shouldRefresh(refresh, activeIteration)) {
            activeIteration = rally.getCurrentIterationForProject(currentProject.objectId)
        }
        return activeIteration
    }

    def getSelectedIteration() {
        if (!selectedIteration) {
            selectedIteration = getActiveIteration()
        }
        return selectedIteration
    }

    def selectIteration(def selectedIteration) {
        this.selectedIteration = selectedIteration
    }

    def getStoriesForSelectedIteration() {
        return rally.getStoriesForIteration(selectedIteration.objectId)
    }

    def shouldRefresh(def refresh, def object) {
        return (refresh || !object)
    }

    def refresh() {
        getCurrentProject(true)
        getActiveIteration(true)
        getFutureIterations(true)
    }

    def initialise() {
        try {
            this.rally = Rally.getInstance(project)
            this.rally.initialise()
            getCurrentUser(true)
            getCurrentProject(true)
            offline = false
        } catch (Exception e) {
            LOG.error("Unable to connect to rally server", e)
            offline = true
        }
    }
}