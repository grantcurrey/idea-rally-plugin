package com.dnat.idea.rally.connector

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.jayway.jsonpath.JsonPath
import com.rallydev.rest.RallyRestApi
import com.rallydev.rest.request.GetRequest
import com.rallydev.rest.request.QueryRequest
import com.rallydev.rest.util.QueryFilter

class Rally {

    RallyRestApi restApi
    RallySettings rallySettings

    public static Rally getInstance(Project project) {
        Rally singleton = ServiceManager.getService(project, Rally.class)
        return singleton != null ? singleton : new Rally(project)
    }

    private Rally(Project project) {
        this.rallySettings = RallySettings.getInstance(project)
        initialise()
    }

    void initialise() {
        restApi = new InsecureRallyRestApi(new URI(rallySettings.url), rallySettings.username, rallySettings.password)
        restApi.setWsapiVersion("1.40")
    }

    def getUserDetails() {
        def request = new GetRequest("/user")
        def response = restApi.get(request)
        String objectJson = response.object.toString()

        return [
                type: "user",
                firstName: JsonPath.read(objectJson, "\$.FirstName"),
                lastName: JsonPath.read(objectJson, "\$.LastName"),
                userName: JsonPath.read(objectJson, "\$.UserName"),
                emailAddress: JsonPath.read(objectJson, "\$.EmailAddress"),
                displayName: JsonPath.read(objectJson, "\$.DisplayName"),
                userProfileId: ((JsonPath.read(objectJson, "\$.UserProfile._ref") =~ /.*\/([0-9]*)\.js/)[0][1])
        ]
    }

    def getCurrentProject() {
        def user = getUserDetails()
        def response = restApi.get(new GetRequest("/userprofile/${user.userProfileId}.js"))
        def projectId = ((JsonPath.read(response.object.toString(), "\$.DefaultProject._ref") =~ /.*\/([0-9]*)\.js/)[0][1])
        String objectJson = restApi.get(new GetRequest("/project/${projectId}")).object.toString()

        return [
                type: "project",
                name: JsonPath.read(objectJson, "\$.Name"),
                objectId: projectId
        ]
    }

    def getFutureIterationsForProject(def projectId) {
        QueryRequest request = new QueryRequest("Iteration")
        request.queryFilter = QueryFilter.and(
                new QueryFilter("project", "=", "project/${projectId}"),
                new QueryFilter("EndDate", ">=", "today"),
                QueryFilter.or(
                        new QueryFilter("state", "=", "Committed"),
                        new QueryFilter("state", "=", "Planning")
                )
        )

        def response = restApi.query(request)

        def results = []
        if (response.totalResultCount > 0) {
            response.results.each { result ->
                def objectJson = result.toString()
                results.add([
                        type: "iteration",
                        name: JsonPath.read(objectJson, "\$.Name") as String,
                        objectId: JsonPath.read(objectJson, "\$.ObjectID") as Long,
                ])
            }
        }
        return results
    }

    def getStoriesForIteration(def iterationId) {
        QueryRequest request = new QueryRequest("hierarchicalrequirement")
        request.queryFilter = new QueryFilter("iteration", "=", "iteration/${iterationId}")
        def response = restApi.query(request)
        def results = []
        if (response.totalResultCount > 0) {
            response.results.each { result ->
                def objectJson = result.toString()
                results.add([
                        type: "story",
                        formattedId: JsonPath.read(objectJson, "\$.FormattedID") as String,
                        name: JsonPath.read(objectJson, "\$.Name") as String,
                        scheduleState: JsonPath.read(objectJson, "\$.ScheduleState") as String
                ])
            }
        }
        return results
    }

    def getCurrentIterationForProject(def projectId) {
        QueryRequest request = new QueryRequest("Iteration")
        request.queryFilter = QueryFilter.and(new QueryFilter("project", "=", "project/${projectId}"),new QueryFilter("state", "=", "Committed"), new QueryFilter("EndDate", ">=", "today"))
        def response = restApi.query(request)
        def results = [:]
        if (response.totalResultCount > 0) {
            def objectJson = response.results.get(0).toString()
            results.type = "iteration"
            results.name = JsonPath.read(objectJson, "\$.Name") as String
            results.objectId = JsonPath.read(objectJson, "\$.ObjectID") as Long
        }
        return results
    }
}
