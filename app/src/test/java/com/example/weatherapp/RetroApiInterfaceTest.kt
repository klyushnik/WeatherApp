package com.example.weatherapp

import com.example.weatherapp.api.RetroApiInterface
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(JUnit4::class)
class RetroApiInterfaceTest {

    lateinit var inter: RetroApiInterface
    lateinit var mockServer: MockWebServer
    lateinit var gson : Gson


    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        mockServer = MockWebServer()
        gson = Gson()
        inter = Retrofit.Builder()
            .baseUrl(mockServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(RetroApiInterface::class.java)
    }

    @Test
    fun getWeather() {
        var mockRes = MockResponse()
        mockServer.enqueue(mockRes.setBody("[]"))


        assertEquals("/data/2.5/onecall?", "/data/2.5/onecall?")

    }

    @After
    fun destroy() {
        mockServer.shutdown()
    }

    }
