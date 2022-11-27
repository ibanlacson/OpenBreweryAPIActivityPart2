package com.auf.cea.openbreweryapiactivity.services.helper

class HelperClass {
    companion object {
        fun concatLocation(
            street: String?, address2: String?, address3: String?,
            city: String?, state: String?, province: String?,
            country: String?
        ): String {
            val finalStreet = if (street == null) {
                ""
            } else {
                "$street, "
            }
            val finalAdd2 = if (address2 == null) {
                ""
            } else {
                "$address2, "
            }
            val finalAdd3 = if (address3 == null) {
                ""
            } else {
                "$address3, "
            }
            val finalCity = if (city == null) {
                ""
            } else {
                "$city, "
            }
            val finalState = if (state == null) {
                ""
            } else {
                "$state, "
            }
            val finalProvince = if (province == null) {
                ""
            } else {
                "$province, "
            }
            val finalCountry = if (country == null) {
                ""
            } else {
                "$country, "
            }

            return finalStreet + finalAdd2 + finalAdd3 + finalCity + finalState + finalProvince + finalCountry
        }

        fun checkNull(variable:Any?):String {
            if (variable == null) {
                return "N/A"
            } else {
                return variable as String
            }
        }
    }
}