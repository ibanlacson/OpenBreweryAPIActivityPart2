package com.auf.cea.openbreweryapiactivity.services.helper

class HelperClass {
    companion object {
        fun concatLocation(
            street: String, address2: Any, address3: Any,
            city: String, state: String, province: String,
            country: String
        ): String {

            address2 as String
            address3 as String

            val finalStreet = if (street.isBlank()) {
                ""
            } else {
                "$street, "
            }
            val finalAdd2 = if (address2.isBlank()) {
                ""
            } else {
                "$address2, "
            }
            val finalAdd3 = if (address3.isBlank()) {
                ""
            } else {
                "$address3, "
            }
            val finalCity = if (city.isBlank()) {
                ""
            } else {
                "$city, "
            }
            val finalState = if (state.isBlank()) {
                ""
            } else {
                "$state, "
            }
            val finalProvince = if (province.isBlank()) {
                ""
            } else {
                "$province, "
            }
            val finalCountry = if (country.isBlank()) {
                ""
            } else {
                "$country, "
            }

            return finalStreet + finalAdd3 + finalCity + finalState + finalProvince + finalCountry
        }
    }
}