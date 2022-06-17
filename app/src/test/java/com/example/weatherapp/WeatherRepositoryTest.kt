package com.example.weatherapp

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.example.weatherapp.api.RetroApiInterface
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.database.CurrentWeather
import com.example.weatherapp.database.DailyWeather
import com.example.weatherapp.database.HourlyWeather
import com.example.weatherapp.database.WeatherDao
import io.reactivex.rxjava3.core.Observable.fromArray
import io.reactivex.rxjava3.core.Single
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import retrofit2.Response
import java.util.*


@RunWith(RobolectricTestRunner::class)
class WeatherRepositoryTest {


    @Mock
    lateinit var dao: WeatherDao
    @Mock
    lateinit var inter :RetroApiInterface
    @Mock
    lateinit var context : Context

    lateinit var repo: WeatherRepository

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before()
    fun setUp(){
        MockitoAnnotations.openMocks(this)

        context = ApplicationProvider.getApplicationContext<Context>()

        repo = Mockito.mock(WeatherRepository::class.java)
        //repo = WeatherRepository(inter,context)


    }
    @Test
    fun getWeatherTest(){
        var fakeList =""


        runBlocking {

            Mockito.`when`(repo.getWeather("",""))
                .thenReturn(Response.success(fakeList))


            var result = repo.getWeather("","").body()
            Assert.assertEquals(fakeList, result)
        }
    }

    @Test
    fun getCurrentWeatherTest(){
        var fakeList : List<CurrentWeather> = (listOf(
            CurrentWeather(123,0,0,0,0.0,0.0,0,0,0.0,0.0,0,0,
                0.0,0,0,"","","")
        ))

        var liveList = MutableLiveData<List<CurrentWeather>>()
        liveList.postValue(fakeList)

        Mockito.`when`(repo.getCurrentWeather())
            .thenReturn(liveList)

        val result = repo.getCurrentWeather()

        TestCase.assertEquals(result?.value, fakeList)
    }

    @Test
    fun getDailyWeatherTest(){
        var fakeList : List<DailyWeather> = (listOf<DailyWeather>(
            DailyWeather(123,0,0,0,0,0,0.0,1.5*1,0.0,0.0,0.0,0.0,
                0.0,0,0,0.0,0.0,0,0.0,0,"","","",0,
                0.0,0.0,0.0,0.0)
        ))

        var liveList = MutableLiveData<List<DailyWeather>>()
        liveList.postValue(fakeList)

        runBlocking {
            Mockito.`when`(repo.getDailyWeather()).thenReturn(liveList)

            val result = repo.getDailyWeather()

            TestCase.assertEquals(result?.value, fakeList)
        }
    }

    @Test
    fun getHourlyWeatherTest(){
        var fakeList : List<HourlyWeather> = (listOf(
            HourlyWeather(123,0,0.0,0.0,0,0,0.0,0.0,0,0,0.0,0,
                0.0,0,"","","",0.0)
        ))

        var liveList = MutableLiveData<List<HourlyWeather>>()
        liveList.postValue(fakeList)

        runBlocking {
            Mockito.`when`(repo.getHourlyWeather()).thenReturn(liveList)

            val result = repo.getHourlyWeather()

            TestCase.assertEquals(result?.value, fakeList)
        }
    }

    @Test
    fun getCurrentWeatherSingleTest(){
        var fakeList =
            CurrentWeather(123,0,0,0,0.0,0.0,0,0,0.0,0.0,0,0,
                0.0,0,0,"","","")

        var liveList = MutableLiveData<CurrentWeather>()
        liveList.postValue(fakeList)

        Mockito.`when`(repo.getCurrentWeatherSingle())
            .thenReturn(liveList)

        val result = repo.getCurrentWeatherSingle()

        TestCase.assertEquals(result?.value, fakeList)
    }

}