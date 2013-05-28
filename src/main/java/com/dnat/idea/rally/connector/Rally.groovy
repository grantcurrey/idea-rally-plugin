package com.dnat.idea.rally.connector

import com.jayway.jsonpath.JsonPath
import com.rallydev.rest.RallyRestApi
import com.rallydev.rest.request.GetRequest
import com.rallydev.rest.request.QueryRequest
import com.rallydev.rest.util.QueryFilter

class Rally {
    def username = "grant.currey@wotifgroup.com"
    def password = "Passw0rd"
    def url = "https://rally.wotifgroup.com"

    RallyRestApi restApi

    static final def Rally INSTANCE = new Rally()

    def cache = [:]

    def Rally() {
        initialise()
    }

    void initialise() {
        restApi = new InsecureRallyRestApi(new URI(url), username, password)
        restApi.setWsapiVersion("1.40")
    }

    def refresh(){
        cache.clear()
    }

    def getUserDetails() {
        if (!cache.containsKey("user")) {
            def request = new GetRequest("/user")
            def response = restApi.get(request)
            String objectJson = response.object.toString()

            cache.user = [
                    firstName: JsonPath.read(objectJson, "\$.FirstName"),
                    lastName: JsonPath.read(objectJson, "\$.LastName"),
                    userName: JsonPath.read(objectJson, "\$.UserName"),
                    emailAddress: JsonPath.read(objectJson, "\$.EmailAddress"),
                    displayName: JsonPath.read(objectJson, "\$.DisplayName"),
                    userProfileId: ((JsonPath.read(objectJson, "\$.UserProfile._ref") =~ /.*\/([0-9]*)\.js/)[0][1])
            ]
        }
        return cache.get("user")
    }

    def getDefaultProject() {
        if (!cache.containsKey("defaultProject")) {
            def user = getUserDetails()
            def response = restApi.get(new GetRequest("/userprofile/${user.userProfileId}.js"))
            def projectId = ((JsonPath.read(response.object.toString(), "\$.DefaultProject._ref") =~ /.*\/([0-9]*)\.js/)[0][1])
            String objectJson = restApi.get(new GetRequest("/project/${projectId}")).object.toString()

            cache.defaultProject = [
                    name: JsonPath.read(objectJson, "\$.Name"),
                    objectId: projectId
            ]
        }
        return cache.get("defaultProject")
    }

    def getCurrentIterationForProject(def projectId) {
        QueryRequest request = new QueryRequest("Iteration")
        request.queryFilter = new QueryFilter("project", "=", "project/${projectId}").and(new QueryFilter("state", "=", "Committed"))
        def response = restApi.query(request)
        if (response.totalResultCount > 0) {
            def objectJson = response.results.get(0).toString()
            return [
                    name: JsonPath.read(objectJson, "\$.Name") as String,
                    objectId: JsonPath.read(objectJson, "\$.ObjectID") as Long,
            ]
        }
    }
}
