package com.dnat.idea.rally.connector

import com.dnat.idea.rally.connector.entity.Iteration
import com.dnat.idea.rally.connector.entity.Story
import com.dnat.idea.rally.connector.entity.User
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

        User user = new User()
        user.firstName = JsonPath.read(objectJson, "\$.FirstName")
        user.lastName = JsonPath.read(objectJson, "\$.LastName")
        user.userName = JsonPath.read(objectJson, "\$.UserName")
        user.emailAddress = JsonPath.read(objectJson, "\$.EmailAddress")
        user.displayName = JsonPath.read(objectJson, "\$.DisplayName")
        user.userProfileId = ((JsonPath.read(objectJson, "\$.UserProfile._ref") =~ /.*\/([0-9]*)\.js/)[0][1]) as Long
        user.objectId = ((JsonPath.read(objectJson, "\$._ref") =~ /.*\/([0-9]*)\.js/)[0][1]) as Long
        user.ref = JsonPath.read(objectJson, "\$._ref")

        return user
    }

    def getCurrentProject() {
        def user = getUserDetails()
        def response = restApi.get(new GetRequest("/userprofile/${user.userProfileId}.js"))
        def projectId = ((JsonPath.read(response.object.toString(), "\$.DefaultProject._ref") =~ /.*\/([0-9]*)\.js/)[0][1])
        String objectJson = restApi.get(new GetRequest("/project/${projectId}")).object.toString()

        return new com.dnat.idea.rally.connector.entity.Project(
                ref: JsonPath.read(objectJson, "\$._ref"),
                objectId: projectId as Long,
                name: JsonPath.read(objectJson, "\$.Name")
        )
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
                results.add(new Iteration(
                        ref: JsonPath.read(objectJson, "\$._ref"),
                        objectId: JsonPath.read(objectJson, "\$.ObjectID") as Long,
                        name: JsonPath.read(objectJson, "\$.Name") as String
                ))
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
                results.add(new Story(
                        ref: JsonPath.read(objectJson, "\$._ref"),
                        objectId: JsonPath.read(objectJson, "\$.ObjectID") as Long,
                        formattedId: JsonPath.read(objectJson, "\$.FormattedID") as String,
                        name: JsonPath.read(objectJson, "\$.Name") as String,
                        scheduleState: JsonPath.read(objectJson, "\$.ScheduleState") as String
                ))
            }
        }
        return results
    }

    def getCurrentIterationForProject(def projectId) {
        QueryRequest request = new QueryRequest("Iteration")
        request.queryFilter = QueryFilter.and(new QueryFilter("project", "=", "project/${projectId}"), new QueryFilter("state", "=", "Committed"), new QueryFilter("EndDate", ">=", "today"))
        def response = restApi.query(request)
        def results = null
        if (response.totalResultCount > 0) {
            def objectJson = response.results.get(0).toString()
            results = new Iteration(ref: JsonPath.read(objectJson, "\$._ref"),
                    objectId: JsonPath.read(objectJson, "\$.ObjectID") as Long,
                    name: JsonPath.read(objectJson, "\$.Name") as String)

        }
        return results
    }
}
