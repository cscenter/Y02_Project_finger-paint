package ru.cscenter.fingerpaint.db

import androidx.room.*

@Entity(indices = [Index(value = ["name"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var name: String,
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
    override fun toString() = name
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
    @ColumnInfo var name: String
) {
    override fun toString() = name
}

@Dao
interface DbAccess {
    @Query("SELECT id, name FROM User")
    fun getAllNames(): List<UserName>

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUser(id: Int): User?

    @Update
    fun setUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(user: User): Long

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
