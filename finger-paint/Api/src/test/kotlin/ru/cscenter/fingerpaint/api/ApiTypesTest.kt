package ru.cscenter.fingerpaint.api

import com.beust.klaxon.KlaxonException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ApiTypesTest {
    @Test
    fun testPatient() {
        val patient = ApiPatient(42, "Leo")
        val string = "{\"id\" : 42, \"name\" : \"Leo\"}"
        assertEquals(string, toJson(patient))
        assertEquals(patient, fromJson<ApiPatient>(string))
    }

    @Test
    fun testPatientName() {
        val patientName = ApiPatientName("Leo")
        val string = "{\"name\" : \"Leo\"}"
        assertEquals(string, toJson(patientName))
        assertEquals(patientName, fromJson<ApiPatientName>(string))
    }

    @Test
    fun testPatientId() {
        val patientId = ApiPatientId(42)
        val string = "{\"id\" : 42}"
        assertEquals(string, toJson(patientId))
        assertEquals(patientId, fromJson<ApiPatientId>(string))
    }

    @Test
    fun testStatistic() {
        val statistic = ApiStatistic(42, "01.01.2020", 3, 23, 3)
        val string = "{\"date\" : \"01.01.2020\", \"patient_id\" : 42," +
                " \"success\" : 3, \"total\" : 23, \"type\" : 3}"
        assertEquals(string, toJson(statistic))
        assertEquals(statistic, fromJson<ApiStatistic>(string))
    }

    @Test
    fun testInvalidInput() {
        val string = "{\"idid\" : 45}"
        assertThrows<KlaxonException> { fromJson<ApiPatientId>(string) }
    }

    @Test
    fun testOddProperties() {
        val string = "{\"id\" : 45, \"name\" : \"Ken\"}"
        assertEquals(ApiPatientId(45), fromJson<ApiPatientId>(string)) // TODO Should fail
    }

    @Test
    fun testArray() {
        val patientIds = listOf(ApiPatientId(42), ApiPatientId(43))
        val string = "[{\"id\" : 42}, {\"id\" : 43}]"
        assertEquals(string, toJsonArray(patientIds))
        assertEquals(patientIds, fromJsonArray<ApiPatientId>(string))
    }
}
