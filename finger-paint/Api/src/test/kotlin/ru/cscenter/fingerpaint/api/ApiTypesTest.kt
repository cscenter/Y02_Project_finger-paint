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
    fun testStatistic() {
        val statistic = ApiStatistic("01.01.2020", 3, 23, 3)
        val string = "{\"date\" : \"01.01.2020\"," +
                " \"success\" : 3, \"total\" : 23, \"type\" : 3}"
        assertEquals(string, toJson(statistic))
        assertEquals(statistic, fromJson<ApiStatistic>(string))
    }

    @Test
    fun testInvalidInput() {
        val string = "{\"idid\" : 45}"
        assertThrows<KlaxonException> { fromJson<ApiPatientName>(string) }
    }

    @Test
    fun testOddProperties() {
        val string = "{\"name\" : \"45\", \"unknown\" : \"Ken\"}"
        assertEquals(ApiPatientName("45"), fromJson<ApiPatientName>(string)) // TODO Should fail
    }

    @Test
    fun testArray() {
        val patientIds = listOf(ApiPatientName("42"), ApiPatientName("43"))
        val string = "[{\"name\" : \"42\"}, {\"name\" : \"43\"}]"
        assertEquals(string, toJsonArray(patientIds))
        assertEquals(patientIds, fromJsonArray<ApiPatientName>(string))
    }
}
