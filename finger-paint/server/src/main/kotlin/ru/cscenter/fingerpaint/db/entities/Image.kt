package ru.cscenter.fingerpaint.db.entities

import javax.persistence.*

@Entity
@Table(name = "Images")
class Image(
    @Column(name = "path", unique = true) var path: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0

    override fun equals(other: Any?) = other is Image && path == other.path
    override fun hashCode() = path.hashCode()
}
