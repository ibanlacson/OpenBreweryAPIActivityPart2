package com.auf.cea.openbreweryapiactivity.realm.operations

import com.auf.cea.openbreweryapiactivity.models.BreweryDBItems
import com.auf.cea.openbreweryapiactivity.realm.database.BreweryRealm
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.executeTransactionAwait
import kotlinx.coroutines.Dispatchers
import kotlin.reflect.typeOf

class BreweryOperations (private var config: RealmConfiguration){
    suspend fun insertBrewery(id: String,
        name: String, type:String,
        location: String,
        phone: String,
        website: String
    ) {
        val realm = Realm.getInstance(config)
        realm.executeTransactionAwait(Dispatchers.IO) { realmTransaction ->
            val brewery = BreweryRealm(id = id, name = name,type = type,location = location, phone = phone, website = website)
            realmTransaction.insert(brewery)
        }
    }

    suspend fun filterBrewery(query: String): ArrayList<BreweryDBItems>{
        val realm = Realm.getInstance(config)
        val realmResults = arrayListOf<BreweryDBItems>()
        realm.executeTransactionAwait(Dispatchers.IO){ realmTransaction ->
            realmResults.addAll(realmTransaction
                .where(BreweryRealm::class.java)
                .contains("id",query, Case.INSENSITIVE)
                .findAll()
                .map {
                    mapBrewery(it)
                })
        }

        return realmResults
    }

    suspend fun retrieveBrewery():ArrayList<BreweryDBItems>{
        val realm = Realm.getInstance(config)
        val realmResults = arrayListOf<BreweryDBItems>()

        realm.executeTransactionAwait (Dispatchers.IO) { realmTransaction ->
            realmResults.addAll(realmTransaction
                .where(BreweryRealm::class.java)
                .findAll()
                .map {
                    mapBrewery(it)
                })
        }
        return realmResults
    }

    suspend fun removeBrewery(breweryID : String){
        val realm = Realm.getInstance(config)
        realm.executeTransactionAwait(Dispatchers.IO){ realmTransaction ->
            val breweryToRemove = realmTransaction
                .where(BreweryRealm::class.java)
                .equalTo("id",breweryID)
                .findFirst()
            breweryToRemove?.deleteFromRealm()
        }
    }

    private fun mapBrewery(brewery: BreweryRealm): BreweryDBItems {
        return BreweryDBItems(
            id = brewery.id,
            name = brewery.name,
            type = brewery.type,
            location = brewery.location,
            phone = brewery.phone,
            website = brewery.website
        )
    }


}