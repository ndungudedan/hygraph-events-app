package com.example.events

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import com.apollographql.apollo3.network.okHttpClient
import kotlinx.coroutines.flow.onEach
import okhttp3.OkHttpClient

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(LoggingInterceptor())
    .build()


val apolloClient = ApolloClient.Builder()
    .serverUrl("https://api-eu-west-2.hygraph.com/v2/clfb1ecj82kiz01t81wxv7rsb/master")
    .addInterceptor(LoggingApolloInterceptor())
    .okHttpClient(okHttpClient = okHttpClient)
    .build()


