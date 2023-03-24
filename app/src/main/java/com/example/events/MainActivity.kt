package com.example.events

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.events.ui.theme.EventsTheme
import com.example.events.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyScreen(viewModel)
                }
            }
        }
    }
}

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MyScreen(viewModel: MainViewModel = MainViewModel()) {
        val isLoadingCategory by viewModel.loadingCategory
        val isLoadingEvents by viewModel.loadingEvents
        val categories by viewModel.categoryData
        val locationResults by viewModel.locationData
        val eventResults by viewModel.eventsData

        if(categories.isEmpty()) {
            LaunchedEffect(viewModel) {
                viewModel.fetchCategories()
            }
        }

        var selectedChips by remember { mutableStateOf(setOf<String>()) }
        var searchText by remember { mutableStateOf("") }
        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text("Events") },

                )
            },
            content = {it->
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    if(isLoadingCategory){
                        CircularProgressIndicator(modifier = Modifier
                            .padding(6.dp)
                            .size(size = 32.dp),
                            color = androidx.compose.ui.graphics.Color.Magenta,
                            )
                    }else if(categories.isNotEmpty()) {
                        if(selectedChips.isEmpty()) {
                            selectedChips = selectedChips.plus(categories.first())
                        }

                        LazyRow(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories.size) { index ->
                                val category = categories.elementAt(index)
                                FilterChip(
                                    selectedIcon = {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Checked Icon")
                                    },
                                    onClick = {
                                        if (!selectedChips.contains(category)) {
                                            selectedChips = selectedChips.plus(category)
                                        } else {
                                            if (selectedChips.size > 1) {
                                                selectedChips = selectedChips.minus(category)
                                            }
                                        }
                                    },
                                    selected = selectedChips.contains(category),
                                ) {
                                    Text(text = category)
                                }
                            }
                        }
                    }

                        OutlinedTextField(
                            value = searchText,
                            onValueChange ={
                                searchText=it
                                if(searchText.length>3){
                                        viewModel.searchLocations(searchText)
                                }
                                           },
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .onFocusChanged { focused ->
                                    if (focused.isFocused && locationResults.isEmpty()) {
                                    }
                                },
                            label = { Text(text = "Search City or Country")},
                            trailingIcon = {
                                IconButton(onClick = {
                                    searchText = ""
                                    viewModel.resetLocationResults()
                                }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                }
                            },

                        )

                        // Search results
                        if (locationResults.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = 120.dp)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                            Popup(
                                alignment = Alignment.Center,
                                properties = PopupProperties(
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true
                                ),
                                content = {
                                    Box(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .height(250.dp)
                                            .background(MaterialTheme.colors.background)
                                    ) {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxWidth(1f)
                                        ) {
                                            items(locationResults.size) { index ->
                                                val location = locationResults.elementAt(index)
                                                ListItem(
                                                    text = { Text(location?.name + "," + location?.country) },
                                                    modifier = Modifier.clickable {
                                                        location?.id?.let { it1 ->
                                                            viewModel.fetchEvents(
                                                                selectedChips,
                                                                it1
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                },
                                onDismissRequest = {}
                            )
                        }

                        }

                    if (isLoadingEvents) {
                        CircularProgressIndicator(modifier = Modifier
                            .padding(6.dp)
                            .size(size = 32.dp),
                            color = androidx.compose.ui.graphics.Color.Magenta,
                        )
                    }
                   else if (eventResults.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(1f)
                        ) {
                            items(eventResults.size) { index ->
                                val event=eventResults.elementAt(index)
                                var desc= "Start: ${event?.start}"
                                if(event?.entities!=null && event.entities.isNotEmpty()){
                                    desc=desc+"\nVenue: "+event.entities.first()?.name
                                }
                                ListItem(
                                    text = { event?.title?.let { it1 -> Text(it1) } },
                                    secondaryText = {
                                        Text(text = desc)
                                    },
                                    trailing = {
                                        if (event?.duration != null) {
                                            Text(text = "Duration: ${event.duration}")
                                        }
                                    },
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    else {
                        // Show loading indicator
                        Text(text="NO EVENTS DATA",modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp))
                    }

                    }

                })

            }

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    EventsTheme {
    }
}