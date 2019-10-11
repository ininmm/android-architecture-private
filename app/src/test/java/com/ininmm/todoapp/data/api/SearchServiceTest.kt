package com.ininmm.todoapp.data.api

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
class SearchServiceTest {

    private lateinit var service: SearchService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchService::class.java)
    }

    @After
    fun dropdown() {
        mockWebServer.shutdown()
    }

    @Test
    fun searchRepoThenResponse() {
        val query = "coil+org:coil-kt"
        enqueueResponse("search-repo.json")
        val response = runBlocking {
            service.searchRepo(query)
        }
        val request = mockWebServer.takeRequest()

        assertThat(request.path, `is`("/search/repositories?q=$query"))
        val items = response.items
        assertThat(items.size, `is`(1))
        assertThat(items[0].name, `is`("coil"))
        assertThat(items[0].fullName, `is`("coil-kt/coil"))
        assertThat(items[0].gitUrl, `is`("git://github.com/coil-kt/coil.git"))
        assertThat(items[0].owner.login, `is`("coil-kt"))
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val classloader = javaClass.classLoader
        val inputStream = classloader.getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}