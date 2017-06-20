# AstroAppDemo
 1  Application uses Google Firebase Realtime database as backend over cloud.
      com.google.firebase:firebase-database:9.6.1
 

2. App uses Otto as an event bus.
    com.squareup:otto:1.3.8

 

3.       Android Volley lib is used for communication with Astro backend.

          com.android.volley:volley:1.0.0

 

4.       App maintain user specific favourites on firebase database by table name as userId. This user is uniq identifier that is obtained once user does social sign on.

 

5.       Facebook sdk is used for social signing and maintaining user specific data.
          com.facebook.android:facebook-android-sdk:4.6.0

 

App maintain a local database of user Favourites, Every time user is logging out it sync local database with Firebase and remove local db.

 

Every time user logging in local db is created and synced with cloud data.
