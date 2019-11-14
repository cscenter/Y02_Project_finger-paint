package ru.cscenter.fingerpaint.db

import androidx.room.*

@Entity(indices = [Index(value = ["firstName", "lastName"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var firstName: String,
    @ColumnInfo var lastName: String,
    @ColumnInfo var figureChooseTotal: Int = 0,
    @ColumnInfo var figureChooseSuccess: Int = 0,
    @ColumnInfo var letterChooseTotal: Int = 0,
    @ColumnInfo var letterChooseSuccess: Int = 0,
    @ColumnInfo var colorChooseTotal: Int = 0,
    @ColumnInfo var colorChooseSuccess: Int = 0,
    @ColumnInfo var coloringTotal: Int = 0,
    @ColumnInfo var coloringSuccess: Int = 0,
    @ColumnInfo var contouringTotal: Int = 0,
    @ColumnInfo var contouringSuccess: Int = 0
) {
    override fun toString() = "$firstName $lastName"
}


@Entity(foreignKeys = [ForeignKey(
    entity = User::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("user_id"))],
    indices = [Index("user_id")])
data class CurrentUser(
    @ColumnInfo(name = "user_id") var userId: Int,
    @PrimaryKey  val id: Int = 0
)

data class UserName(
    @ColumnInfo val id: Int,
    @ColumnInfo var firstName: String,
    @ColumnInfo var lastName: String
) {
    override fun toString() = "$firstName $lastName"
}

@Dao
interface DbAccess {
    @Query("SELECT id, firstName, lastName FROM User")
    fun getAllNames(): List<UserName>

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUser(id: Int): User?

    @Update
    fun setUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE id IN (SELECT user_id FROM CurrentUser)")
    fun getCurrentUser(): User?

    @Update
    fun setCurrentUser(currentUser: CurrentUser)

    @Insert
    fun addCurrentUser(currentUser: CurrentUser)
}

@Database(entities = [User::class, CurrentUser::class], exportSchema = false, version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DbAccess
}
