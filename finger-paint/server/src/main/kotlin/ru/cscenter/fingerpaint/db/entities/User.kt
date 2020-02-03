package ru.cscenter.fingerpaint.db.entities

import javax.persistence.*

@Entity
@Table(name = "Users")
class User(
    @Column(name = "login", unique = true) var login: String,
    @Column(name = "password") var password: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long = 0

    override fun equals(other: Any?) = other is User && login == other.login
    override fun hashCode() = login.hashCode()
}
