package ru.cscenter.fingerpaint.db.entities

import javax.persistence.*

@Entity
@Table(name = "DrawTasks")
class DrawTask(
    @Column(name = "text") var text: String,
    @Column(name = "image_id") var imageId: Long
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0

    override fun equals(other: Any?) = other is DrawTask
            && text == other.text
            && imageId == other.imageId

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + imageId.hashCode()
        return result
    }

}
