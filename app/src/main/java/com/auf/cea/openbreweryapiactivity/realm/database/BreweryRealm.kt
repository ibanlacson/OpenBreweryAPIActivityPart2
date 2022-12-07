package com.auf.cea.openbreweryapiactivity.realm.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class BreweryRealm(
    @PrimaryKey
    var id: String = "",
    @Required
    var name: String = "",
    var type: String = "",
    var location: String = "",
    @Required
    var phone: String = "",
    var website: String = ""
): RealmObject()
