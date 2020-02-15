package ru.cscenter.fingerpaint.db

import androidx.lifecycle.LiveData
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
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("user_id")],
    primaryKeys = ["user_id", "date", "type"]
)
data class Statistic(
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo val date: Long = currentDay(),
    @ColumnInfo val type: GameType,
    @ColumnInfo var total: Int = 0,
    @ColumnInfo var success: Int = 0
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("user_id")]
)
data class CurrentUser(
    @ColumnInfo(name = "user_id") val userId: Int,
    @PrimaryKey val id: Int = 0
)

@Dao
interface DbAccess {

    @Query("SELECT * FROM User")
    fun getUsers(): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun getUser(id: Int): User?

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun setUser(user: User): Int

    @Delete
    suspend fun deleteUser(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM User WHERE id IN (SELECT user_id FROM CurrentUser)")
    fun getCurrentUser(): LiveData<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCurrentUser(currentUser: CurrentUser)

    @Query("SELECT * FROM CurrentUser")
    suspend fun hasCurrentUser(): CurrentUser?

    @Query("SELECT * FROM Statistic WHERE (user_id IN (SELECT user_id FROM CurrentUser)) AND date = :day AND type = :type")
    suspend fun getCurrentUserStatistics(type: GameType, day: Long = currentDay()): Statistic?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistics(statistic: Statistic)

    @Query("SELECT * FROM Statistic WHERE user_id = :id ORDER BY date")
    fun getUserAllStatistics(id: Int): LiveData<List<Statistic>>
}

@Database(
    entities = [User::class, CurrentUser::class, Statistic::class],
    exportSchema = false, version = 1
)
@TypeConverters(TypeConverter::class)
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
