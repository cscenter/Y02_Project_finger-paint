package ru.cscenter.fingerpaint.db

import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Entity(indices = [Index(value = ["name"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var name: String
) {
    override fun toString() = name
}

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("user_id")
    )],
    indices = [Index("user_id")],
    primaryKeys = ["user_id", "date"]
)
data class Statistic(
    @ColumnInfo(name = "user_id") var userId: Int,
    @ColumnInfo var date: Long = currentDay(),
    @ColumnInfo var figureChooseTotal: Int = 0,
    @ColumnInfo var figureChooseSuccess: Int = 0,
    @ColumnInfo var letterChooseTotal: Int = 0,
    @ColumnInfo var letterChooseSuccess: Int = 0,
    @ColumnInfo var figureColorChooseTotal: Int = 0,
    @ColumnInfo var figureColorChooseSuccess: Int = 0,
    @ColumnInfo var letterColorChooseTotal: Int = 0,
    @ColumnInfo var letterColorChooseSuccess: Int = 0,
    @ColumnInfo var drawingTotal: Int = 0,
    @ColumnInfo var drawingSuccess: Int = 0,
    @ColumnInfo var contouringTotal: Int = 0,
    @ColumnInfo var contouringSuccess: Int = 0
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("user_id")
    )],
    indices = [Index("user_id")]
)
data class CurrentUser(
    @ColumnInfo(name = "user_id") var userId: Int,
    @PrimaryKey val id: Int = 0
)

@Dao
interface DbAccess {
    @Query("SELECT id, name FROM User")
    fun getAllNames(): List<User>

    @Query("SELECT * FROM User WHERE id = :id")
    fun getUser(id: Int): User?

    @Query("SELECT id FROM User WHERE name = :name")
    fun getUserId(name: String): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun setUser(user: User): Int

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

    @Query("SELECT * FROM Statistic WHERE user_id = :userId AND date = :day")
    fun getUserStatistics(userId: Int?, day: Long = currentDay()): Statistic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatistics(statistic: Statistic)

    @Query("SELECT * FROM Statistic WHERE user_id = :id ORDER BY date")
    fun getUserAllStatistics(id: Int): List<Statistic>
}

@Database(
    entities = [User::class, CurrentUser::class, Statistic::class],
    exportSchema = false, version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): DbAccess
}

fun currentDay(): Long {
    val localCalendar = GregorianCalendar.getInstance()
    val year = localCalendar.get(Calendar.YEAR)
    val month = localCalendar.get(Calendar.MONTH)
    val day = localCalendar.get(Calendar.DAY_OF_MONTH)

    val utcCalendar = GregorianCalendar(TimeZone.getTimeZone("UTC"))
    utcCalendar.set(Calendar.YEAR, year)
    utcCalendar.set(Calendar.MONTH, month)
    utcCalendar.set(Calendar.DAY_OF_MONTH, day)
    utcCalendar.set(Calendar.HOUR_OF_DAY, 12)
    utcCalendar.set(Calendar.MINUTE, 0)
    utcCalendar.set(Calendar.SECOND, 0)
    utcCalendar.set(Calendar.MILLISECOND, 0)
    return utcCalendar.timeInMillis
}

private val dateFormat = SimpleDateFormat("MMM-dd", Locale.ENGLISH)

fun dateToString(date: Long): String = dateFormat.format(Date(date))
