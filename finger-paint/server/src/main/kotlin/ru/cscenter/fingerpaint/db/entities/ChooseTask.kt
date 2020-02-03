package ru.cscenter.fingerpaint.db.entities

import javax.persistence.*

@Entity
@Table(name = "ChooseTasks")
class ChooseTask(
    @Column(name = "text") var text: String,
    @Column(name = "correct_id") var correctImageId: Long,
    @Column(name = "incorrect_id") var incorrectImageId: Long
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0

    override fun equals(other: Any?) = other is ChooseTask
            && text == other.text
            && correctImageId == other.correctImageId
            && incorrectImageId == other.incorrectImageId

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + correctImageId.hashCode()
        result = 31 * result + incorrectImageId.hashCode()
        return result
    }
}
