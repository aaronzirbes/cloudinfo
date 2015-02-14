package org.zirbes.cloudinfo

import com.fasterxml.jackson.databind.ObjectMapper

import com.squareup.okhttp.Cache
import com.squareup.okhttp.OkHttpClient

import groovy.transform.CompileStatic

import retrofit.RequestInterceptor
import retrofit.RestAdapter
import retrofit.client.OkClient

@CompileStatic
class RetrofitClientBuilder {

    String uri = 'http://localhost:8080'
    OkHttpClient okHttpClient = new OkHttpClient()

    RestAdapter.LogLevel logLevel = RestAdapter.LogLevel.BASIC

    RetrofitClientBuilder withEndpoint(String uri) {
        this.uri = uri
        return this
    }

    RetrofitClientBuilder withCache(Boolean useCaching, Integer cacheSizeInBytes = 10 * 1024) {
        if (useCaching) {
            this.okHttpClient = new OkHttpClient()
            String cacheFileLocation = System.getProperty('java.io.tmpdir')
            File cacheDir = new File(cacheFileLocation, UUID.randomUUID().toString())
            Cache cache = new Cache(cacheDir, cacheSizeInBytes)
            this.okHttpClient.setCache(cache)
        } else {
            this.okHttpClient = new OkHttpClient()
        }
        return this
    }

    @SuppressWarnings('UnnecessaryPublicModifier')
    public <T> T build(Class<T> clazz) {
        OkClient clientProvider = new OkClient(okHttpClient)
        RestAdapter.Builder builder = new RestAdapter.Builder()
            .setClient(clientProvider)
            .setEndpoint(uri)
            .setErrorHandler(new RetrofitClientErrorHandler())
            .setLog(new RetrofitLogger())
            .setLogLevel(logLevel)

        RestAdapter restAdapter = builder.build()

        return (T) restAdapter.create(clazz)
    }
}

