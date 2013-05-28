package com.dnat.idea.rally.connector

@Singleton
class RallySession {
    def currentUser
    def currentProject
    def futureIterations
    def activeIteration

    def selectedIteration

    def getCurrentUser(boolean refresh = false) {
        if (refresh || currentUser == null) {
            currentUser = Rally.instance.userDetails
        }
        return currentUser
    }

    def getCurrentProject(boolean refresh = false) {
        if (refresh || currentProject == null) {
            currentProject = Rally.instance.currentProject
        }
        return currentProject
    }

    def getFutureIterations(boolean refresh = false) {
        if (refresh || futureIterations == null) {
            futureIterations = Rally.instance.getFutureIterationsForProject(currentProject.objectId)
        }
        return futureIterations
    }

    def getActiveIteration(boolean refresh = false) {
        if (refresh || activeIteration == null) {
            activeIteration = Rally.instance.getCurrentIterationForProject(currentProject.objectId)
        }
        return activeIteration
    }

    def initialise(){
        getCurrentUser(true)
        getCurrentProject(true)
    }
}