package com.dnat.idea.rally.connector

import org.apache.log4j.Logger

@Singleton
class RallySession {

    private static final Logger LOG = Logger.getLogger(RallySession.class)

    def currentUser
    def currentProject
    def futureIterations
    def activeIteration

    def selectedIteration

    def offline = true

    def getCurrentUser(boolean refresh = false) {
        if (shouldRefresh(refresh, currentUser)) {
            currentUser = Rally.instance.userDetails
        }
        return currentUser
    }

    def getCurrentProject(boolean refresh = false) {
        if (shouldRefresh(refresh, currentProject)) {
            currentProject = Rally.instance.currentProject
        }
        return currentProject
    }

    def getFutureIterations(boolean refresh = false) {
        if (shouldRefresh(refresh, futureIterations)) {
            futureIterations = Rally.instance.getFutureIterationsForProject(currentProject.objectId)
        }
        return futureIterations
    }

    def getActiveIteration(boolean refresh = false) {
        if (shouldRefresh(refresh, activeIteration)) {
            activeIteration = Rally.instance.getCurrentIterationForProject(currentProject.objectId)
        }
        return activeIteration
    }

    def getSelectedIteration(){
        if(!selectedIteration){
            selectedIteration = getActiveIteration()
        }
        return selectedIteration
    }

    def selectIteration(def selectedIteration) {
        this.selectedIteration = selectedIteration
    }

    def getStoriesForSelectedIteration() {
        return Rally.instance.getStoriesForIteration(selectedIteration.objectId)
    }

    def shouldRefresh(def refresh, def object) {
        return (refresh || !object)
    }

    def refresh() {
        getCurrentProject(true)
        getActiveIteration(true)
        getFutureIterations(true)
    }

    def initialise(RallySettings rallySettings) {
        try {
            if (rallySettings) {
                Rally.instance.initialise(rallySettings)
            }
            getCurrentUser(true)
            getCurrentProject(true)
            offline = false
        } catch (Exception e) {
            LOG.error("Unable to connect to rally server", e)
            offline = true
        }
    }
}