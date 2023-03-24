package com.example.events.viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.example.events.apolloClient
import com.example.events.models.CategoryQuery
import com.example.events.models.EventsQuery
import com.example.events.models.LocationQuery
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {


    val categoryData: MutableState<List<String>> = mutableStateOf(emptyList())
    val locationData: MutableState<
            List<LocationQuery.Result?>> = mutableStateOf(emptyList())
    val loadingCategory: MutableState<Boolean> = mutableStateOf(false)
    val loadingEvents: MutableState<Boolean> = mutableStateOf(false)
    val eventsData: MutableState<List<EventsQuery.Result?>> = mutableStateOf(emptyList())

    fun fetchCategories() {
        try {
            loadingCategory.value = true
            viewModelScope.launch {
                categoryData.value = apolloClient
                    .query(CategoryQuery())
                    .execute().dataAssertNoErrors.eventCategories?.first()?.categories
                    ?: emptyList()
                loadingCategory.value = false
            }
        } catch (exception: ApolloException) {
            exception.localizedMessage?.let { Log.e("Apollo: ", it) }
            loadingCategory.value = false
        }

    }

    fun searchLocations(location: String) {
        try {
            viewModelScope.launch {

                locationData.value = apolloClient
                    .query(LocationQuery(location = location))
                    .execute().dataAssertNoErrors.eventLocations?.first()?.places?.results
                    ?: emptyList()
            }
        } catch (exception: ApolloException) {
            exception.localizedMessage?.let { Log.e("Apollo: ", it) }
        }
    }

    fun fetchEvents(selectedCategory: Set<String>, location_id: String) {
        try {
            if (selectedCategory.isNotEmpty()) {
                locationData.value = emptyList()
                loadingEvents.value = true
                var category: String = ""
                selectedCategory.forEach {
                    if (category.isEmpty()) {
                        category = "$category$it"
                    } else {
                        category = "$category,$it"
                    }
                }
                viewModelScope.launch {
                    eventsData.value = apolloClient
                        .query(EventsQuery(category, location_id.toInt()))
                        .execute().dataAssertNoErrors.listEvents?.first()?.allEvents?.results
                        ?: emptyList()
                    loadingEvents.value = false
                }
            }
        } catch (exception: ApolloException) {
            exception.localizedMessage?.let { Log.e("Apollo: ", it) }
            loadingEvents.value = false
        }


    }

    fun resetLocationResults() {
        locationData.value = emptyList()
    }
}
