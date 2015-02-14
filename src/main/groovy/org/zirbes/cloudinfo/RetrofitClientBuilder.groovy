package org.zirbes.cloudinfo

import com.fasterxml.jackson.databind.ObjectMapper

import com.squareup.okhttp.Cache
import com.squareup.okhttp.OkHttpClient

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

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

    @SuppressWarnings('UnnecessaryPublicModifier')
    public <T> T build(Class<T> clazz) {
        okHttpClient.setConnectTimeout(3, TimeUnit.SECONDS)
        okHttpClient.setReadTimeout(3, TimeUnit.SECONDS)
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

