package com.agrotec.guinea.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ─── ENTITIES ────────────────────────────────────────────────────────────────

@Entity(tableName = "diagnoses")
data class DiagnosisEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropName: String,
    val pestName: String,
    val confidence: String,
    val treatment: String,
    val aiAdvice: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String = ""
)

@Entity(tableName = "treatments")
data class TreatmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropName: String,
    val pestName: String,
    val symptoms: String,
    val treatment: String,
    val prevention: String,
    val severityLevel: Int // 1=Low, 2=Medium, 3=High
)

@Entity(tableName = "market_prices")
data class MarketPriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cropName: String,
    val priceXAF: Double,
    val unit: String,
    val trend: String, // "up", "down", "stable"
    val lastUpdated: String
)

// ─── DAOs ────────────────────────────────────────────────────────────────────

@Dao
interface DiagnosisDao {
    @Insert
    suspend fun insert(diagnosis: DiagnosisEntity): Long

    @Query("SELECT * FROM diagnoses ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<DiagnosisEntity>>

    @Query("SELECT * FROM diagnoses ORDER BY timestamp DESC LIMIT 10")
    suspend fun getRecent(): List<DiagnosisEntity>

    @Query("DELETE FROM diagnoses WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT COUNT(*) FROM diagnoses")
    suspend fun count(): Int
}

@Dao
interface TreatmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(treatment: TreatmentEntity)

    @Query("SELECT * FROM treatments WHERE cropName LIKE '%' || :crop || '%'")
    suspend fun getForCrop(crop: String): List<TreatmentEntity>

    @Query("SELECT * FROM treatments WHERE pestName LIKE '%' || :pest || '%' LIMIT 1")
    suspend fun getForPest(pest: String): TreatmentEntity?

    @Query("SELECT * FROM treatments")
    suspend fun getAll(): List<TreatmentEntity>

    @Query("SELECT COUNT(*) FROM treatments")
    suspend fun count(): Int
}

@Dao
interface MarketPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(price: MarketPriceEntity)

    @Query("SELECT * FROM market_prices ORDER BY cropName ASC")
    fun getAllFlow(): Flow<List<MarketPriceEntity>>

    @Query("SELECT COUNT(*) FROM market_prices")
    suspend fun count(): Int
}

// ─── DATABASE ────────────────────────────────────────────────────────────────

@Database(
    entities = [DiagnosisEntity::class, TreatmentEntity::class, MarketPriceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diagnosisDao(): DiagnosisDao
    abstract fun treatmentDao(): TreatmentDao
    abstract fun marketPriceDao(): MarketPriceDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "agrotec_db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
