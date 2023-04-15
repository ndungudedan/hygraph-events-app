# Hygraph Events App
This is an android application that uses [Hygraph`s](https://hygraph.com/) GraphQL API to query for events happening around the world. The Hygraph content is sourced from [PredictHQ](https://predicthq.com) events API. 

This project is a follow up of the tutorial on **How to Build an Event App with Hygraph**.

## Setup
- Clone and open the project in your machine.
- Open the file `ApolloClient.kt` and replace the following line of code with your authentication token generated from Hygraph.

```

const val authToken="<Auth Token Generated From Hygraph>"

```
- Run the app
