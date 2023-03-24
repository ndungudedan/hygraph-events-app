package com.example.events

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import com.apollographql.apollo3.network.okHttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class LoggingApolloInterceptor: ApolloInterceptor {

    override fun <D : Operation.Data> intercept(
        request: ApolloRequest<D>,
        chain: ApolloInterceptorChain
    ): Flow<ApolloResponse<D>> {
        return chain.proceed(request).onEach { response ->
            Log.d("Apollo: ","Received response for ${request.operation.name()}: ${response.data}")
        }
    }
}

internal class HttpInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val t1 = System.nanoTime()
        Log.i("REQUEST: ", request.method+"  "+request.url.toString())
        val response: Response = chain.proceed(request)
        Log.i("response:",response.code.toString()+" "+response.networkResponse?.message+" "+response.message)

        return response
    }
}


val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpInterceptor())
    .build()

const val authToken="<Auth Token Generated From Hygraph>"

val apolloClient = ApolloClient.Builder()
    .serverUrl("https://api-eu-west-2.hygraph.com/v2/clfb1ecj82kiz01t81wxv7rsb/master")
    .addInterceptor(LoggingApolloInterceptor())
    .okHttpClient(okHttpClient = okHttpClient)
    .addHttpHeader("Authorization", "Bearer $authToken")
    .build()


