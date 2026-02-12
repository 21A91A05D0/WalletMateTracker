package com.example.walletmatetracker.data.local.database



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.walletmatetracker.data.local.dao.ExpenseDao
import com.example.walletmatetracker.data.local.dao.UserDao
import com.example.walletmatetracker.data.local.entity.ExpenseEntity
import com.example.walletmatetracker.data.local.entity.UserEntity


@Database(
    entities = [ExpenseEntity::class,
    UserEntity::class],
    version = 5, //after block 2 //3 - after image  // 4 after userlogin 5. after name add
    exportSchema = false
)
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    abstract fun userDao(): UserDao


    companion object {

        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "walletmate_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
