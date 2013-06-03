package com.dnat.idea.rally.connector

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import org.apache.log4j.Logger

class RallySession {

    private static final Logger LOG = Logger.getLogger(RallySession.class)

    public static RallySession getInstance(Project project) {
        RallySession singleton = ServiceManager.getService(project, RallySession.class)
        return singleton != null ? singleton : new RallySession(project)
    }

    def currentUser
    def currentProject
    def futureIterations
    def activeIteration

    def selectedIteration

    def offline = true

    Project project
    Rally rally

    private RallySession(Project project) {
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