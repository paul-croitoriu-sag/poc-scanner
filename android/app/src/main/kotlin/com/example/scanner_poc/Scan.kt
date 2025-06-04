package com.example.scanner_poc


class Scan(private val data: String, private val symbology: String, private val dateTime: String)
{
    fun toJson(): String{
        return "{\"scanData\":\"$data\",\"symbology\":\"$symbology\",\"dateTime\":\"$dateTime\"}"
    }
}