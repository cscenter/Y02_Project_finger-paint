package ru.cscenter.fingerpaint.db.entities

import ru.cscenter.fingerpaint.api.ApiStatistic
import java.io.Serializable
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "Statistics")
class Statistic(
    @EmbeddedId var id: StatisticId,
    @Column(name = "total") var total: Int,
    @Column(name = "success") var success: Int
) {
    override fun equals(other: Any?) = other is Statistic
            && id == other.id
            && total == other.total
            && success == other.success

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + total
        result = 31 * result + success
        return result
    }

    fun toApiStatistic() = ApiStatistic(id.patientId, id.date.toString(), id.type, total, success)
}

@Embeddable
class StatisticId(
    @Column(name = "patient_id") var patientId: Long,
    @Column(name = "date") var date: Date,
    @Column(name = "type") var type: Int
) : Serializable {
    override fun equals(other: Any?) = other is StatisticId
            && patientId == other.patientId
            && date == other.date
            && type == other.type

    override fun hashCode(): Int {
        var result = patientId.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

}
