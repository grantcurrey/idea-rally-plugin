package com.dnat.idea.rally.connector.entity

class Project extends RallyEntity{
    String name
}

class User extends RallyEntity{
    String firstName
    String lastName
    String userName
    String emailAddress
    String displayName
    Long userProfileId
}

class Story extends RallyEntity{
    String name
    String formattedId
    String scheduleState
    List<Task> tasks = []
}

class Task extends RallyEntity{
    String name
}

class Iteration extends RallyEntity{
    String name
}

class RallyEntity {
    Long objectId
    String ref
}

