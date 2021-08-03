package com.killua.ideenplattform.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock

@RunWith(JUnit4::class)
class MainRepositoryImplTest {

   private lateinit var repo: MainRepositoryImpl
/*
    private val validLocation = "Helsinki"
    private val invalidLocation = "Helsinkii"
   // private val successResource = Resource.success(Weather(TempData(1.0, 1), "test"))
    private val errorResource = Resource.error("Unauthorised", null)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @ObsoleteCoroutinesApi
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        repo = mock()
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `when getWeather is called with valid location, then observer is updated with success`() =
        runBlocking {
            viewModel.weather.observeForever(weatherObserver)
            viewModel.getWeather(validLocation)
            delay(10)
            verify(weatherRepository).getWeather(validLocation)
            verify(weatherObserver, timeout(50)).onChanged(Resource.loading(null))
            verify(weatherObserver, timeout(50)).onChanged(successResource)
        }

*/
    @Test
    fun login() {
    }

    @Test
    fun getAllUsers() {
    }

    @Test
    fun createUser() {
    }

    @Test
    fun updateUser() {
    }

    @Test
    fun getMe() {
    }

    @Test
    fun getUserId() {
    }

    @Test
    fun uploadUserImage() {
    }

    @Test
    fun deleteImageOfUser() {
    }

    @Test
    fun updateMangerStatus() {
    }

    @Test
    fun getAllCategories() {
    }

    @Test
    fun getCategoryWithId() {
    }

    @Test
    fun createNewIdea() {
    }

    @Test
    fun getAllIdeas() {
    }

    @Test
    fun getIdeaWithId() {
    }

    @Test
    fun updateIdeaWithId() {
    }

    @Test
    fun deleteIdeaWithId() {
    }

    @Test
    fun searchIdeal() {
    }

    @Test
    fun releaseIdea() {
    }

    @Test
    fun createComment() {
    }

    @Test
    fun getComments() {
    }

    @Test
    fun deleteComments() {
    }

    @Test
    fun postRating() {
    }

    @Test
    fun deleteRating() {
    }

    @Test
    fun uploadImageIdea() {
    }

    @Test
    fun getApi() {
    }
}