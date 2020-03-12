package ru.cscenter.fingerpaint.db.entities

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Users")
class User(
    @Id @Column(name = "id") var id: String
) {
    override fun equals(other: Any?) = other is User && id == other.id
    override fun hashCode() = id.hashCode()
}
