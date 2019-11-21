package ru.cscenter.fingerpaint.db

import androidx.room.*

@Entity(indices = [Index(value = ["name"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var name: String
) {
    override fun toString() = name
}

@Entity(foreignKeys = [ForeignKey(
    entity = User::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("user_id"))],
    indices = [Index("user_id")],
    primaryKeys = ["user_id", "date"]
    )
data class Statistic(
    @ColumnInfo(name = "user_id") var userId: Int,
    @ColumnInfo var date: Int = currentDay(),
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
)

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

    @Query("SELECT * FROM Statistic WHERE user_id = :userId ORDER BY date LIMIT 1")
    fun getUserStatistics(userId: Int?): Statistic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatistics(statistic: Statistic)

    @Query("SELECT * FROM Statistic WHERE user_id = :id ORDER BY date")
    fun getUserAllStatistics(id: Int): List<Statistic>
}

@Database(entities = [User::class, CurrentUser::class, Statistic::class],
    exportSchema = false, version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DbAccess
}

private const val MILLIS_PER_DAY = 1000 * 60 * 60 * 24

fun timeToDate(millis: Long): Int = (millis / MILLIS_PER_DAY).toInt()

fun currentDay() = timeToDate(System.currentTimeMillis())
