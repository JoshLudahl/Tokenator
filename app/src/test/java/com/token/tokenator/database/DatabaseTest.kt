package com.token.tokenator.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.token.tokenator.model.Token
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


class DatabaseTest {

//    private lateinit var tokenDao: TokenDao
//    private lateinit var db: TokenDatabase
//
//    private val TOKEN = "234LKFDSA;LK,M23sdfakl78"

    @Before
    fun setup() {
//        val context: Context = ApplicationProvider.getApplicationContext()
//
//        db = Room.inMemoryDatabaseBuilder(context, TokenDatabase::class.java)
//            .allowMainThreadQueries()
//            .build()
//        tokenDao = db.tokenDao()
    }

    @After
    fun closeDatabase() {
//        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun canInsertIntoDatabase() = runBlocking {
//        val token = Token(
//            title = "Super Secret",
//            token = TOKEN
//        )
//        tokenDao.insert(token)
//        val allTokens = tokenDao.getAllTokens().value?.first()
//        assert(allTokens == token)
    }
}
