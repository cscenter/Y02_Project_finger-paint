package ru.cscenter.fingerpaint.db.entities

import javax.persistence.*

@Entity
@Table(
    name = "Patients",
    uniqueConstraints = [UniqueConstraint(columnNames = ["name", "user_id"])]
)
class Patient(
    @Column(name = "name") var name: String,
    @Column(name = "user_id") var userId: Long
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0

    override fun equals(other: Any?) = other is Patient && name == other.name && userId == other.userId
    override fun hashCode() = 31 * name.hashCode() + userId.hashCode()
}
