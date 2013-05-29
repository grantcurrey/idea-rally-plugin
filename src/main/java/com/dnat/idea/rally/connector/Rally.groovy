package com.dnat.idea.rally.connector

import com.jayway.jsonpath.JsonPath
import com.rallydev.rest.RallyRestApi
import com.rallydev.rest.request.GetRequest
import com.rallydev.rest.request.QueryRequest
import com.rallydev.rest.util.QueryFilter

@Singleton
class Rally {

    RallyRestApi restApi

    def Rally() {
    }

    void initialise(RallySettings rallySettings) {
        restApi = new InsecureRallyRestApi(new URI(rallySettings.url), rallySettings.username, rallySettings.password)
        restApi.setWsapiVersion("1.40")
    }

    def getUserDetails() {
        def request = new GetRequest("/user")
        def response = restApi.get(request)
        String objectJson = response.object.toString()

        return [
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
                name: JsonPath.read(objectJson, "\$.Name"),
                objectId: projectId
        ]
    }

    def getFutureIterationsForProject(def projectId) {
        QueryRequest request = new QueryRequest("Iteration")
        request.queryFilter = QueryFilter.and(
                new QueryFilter("project", "=", "project/${projectId}"),
                new QueryFilter("StartDate", ">", "today"),
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
                        name: JsonPath.read(objectJson, "\$.Name") as String,
                        objectId: JsonPath.read(objectJson, "\$.ObjectID") as Long,
                ])
            }
        }
        return results
    }

    def getCurrentIterationForProject(def projectId) {
        QueryRequest request = new QueryRequest("Iteration")
        request.queryFilter = new QueryFilter("project", "=", "project/${projectId}").and(new QueryFilter("state", "=", "Committed"))
        def response = restApi.query(request)
        def results = [:]
        if (response.totalResultCount > 0) {
            def objectJson = response.results.get(0).toString()
            results.name = JsonPath.read(objectJson, "\$.Name") as String
            results.objectId = JsonPath.read(objectJson, "\$.ObjectID") as Long
        }
        return results
    }
}
