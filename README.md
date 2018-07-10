# WeatherForecast

![My image](https://github.com/kaustubhkp/WeatherForecast/blob/master/screenshot/screenshot.jpeg)

# Quick Start:
- Clone project<br>
- Build and Run<br>

# External Libraries:
- RxJava <br>
- Retrofit <br>
- Dagger <br>
- Butter knife <br>

# Architecture:<br>
This project uses MVP pattern. It allowed for rapid iteration and it would seamlessly support any unit or instrumented tests down the line due to the separation of concerns. The project is further organised into different modules based on functionality and their usage and classification is explained below. These sections also feature any improvements that could be done to these respective modules. 

<br>

# App:
Initialises Dagger’s parent component to make them available for the entire lifecycle of the app.<br>

Improvements: Implementing LeakCanary to detect memory leaks

# Dagger:<br>
Dependency Injection is done via Dagger. AppComponent serves as the parent component and exposes the Retrofit Service and FusedLocationProviderClient its dependent components. All the components provided here are scoped to be a Singleton and the dependent components (implemented for each screen) have scopes restricting them to their respective modules, enabling modularity of the codebase. <br>

Improvements: Using Dagger-Android to make the activities more agnostic about their injections. This would also reduce the dependency of Activity having to provide the view to initialise the dependent module. <br>

# Home:
<br>
Currently, the only screen of the app. Firstly It will check internet connection and then ask user for location permissions. Once user allow permission then It will fetch weather prediction from API based on users lat/long. It uses a normal view to display the results fetched from the API. Picasso is used to display the weather icon on screen, offloading the memory and cache management to the library. A HomeComponent is constructed via Dagger and serves as the dependent component and is responsible for injecting presenter, view and other required components for this module. The presenter chooses whether to subscribe to the observable returned by API based on network connectivity and manages UI state during and after the connection.  <br>
Improvements: Creating a repository, so the presenter can be agnostic about the source of data. The repository will also manage storing, caching and retrieval of data, offloading those functions off the presenter. Designing a better UI with colour changes (Using Palette support library).<br>

# Model:
<br>
POJOs representing the response model from the API.<br>

# API Key:
<br>
To store Open Weather Map API key securely on device I put it inside gradle.properties and access it via BuildConfig file at runtime. As gradle.properties is not part of apk when we create release apk so I used this approach to save API key.<br>
 There are other ways as well to achieve this which are, <br>
 1. Get it from server (In our case It's not possible)<br>
 2. Store it via NDK c++ file (95% It will be secure.. I didn't took this approach because I am not sure NDK is setup on reviewers machine)<br>
 There are some other ways as well <br>


# Net:
<br>
Interface for Retrofit to communicate with the API. The Retrofit instance provided by Dagger uses OkHttp’s and uses RxJava adapter to return the results in form of an Observable. <br>

# Lint:
<br>
Lint ran on the code and removed most of the error's and warnings <br>

# General Improvements:
<br>

- Tests. Tests. Tests. Unit tests for the business logic and instrumentation tests for the UI <br>

- Support for landscape/tablet UI <br>

- Instead of showing data from API directly put it into database via Room and then show it from database to UI so it will be single source of truth<br>


