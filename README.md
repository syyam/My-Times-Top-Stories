# My-Times-Top-Stories

## GOAL
A single activity app presenting a list of all New York Times Top Stories in a master view, with a
detail view of each story.

#### Classes have been designed in such a way that it could be inherited and maximize the code reuse.

### The app has following packages:
1. **data**: It contains all the data accessing and manipulating components.
2. **di**: Dependency providing classes using Dagger2.
3. **ui**: View classes along with their corresponding ViewModel.
4. **utils**: Utility classes.

### Libraries
- [Jetpack]
  - [Viewmodel]
  - [View Binding]
  - [Room]
  - [Navigation Components]
- [Retrofit]
- [Gson]
- [okhttp-logging-interceptor]
- [Coroutines] 
- [Flow] 
- [Truth]
- [Material Design]
- [Espresso]
- [mockK]
- [JUnit Rules]
- [Glide]
- [Hilt-Dagger]
- [Hilt-ViewModel]

### Running the tests

The application contains unit tests that run on the local machine and instrumented tests that 
require the use of an emulator or connected Android device. 

1. Run the task `./gradlew connectedCheck` to run both local unit tests and instrumented tests
1. Run the task `./gradlew test` for local unit tests
1. Locate the test coverage results at `/app/build/reports/coverage/androidTest/debug`
1. Locate the local unit test results at
    - HTML test result files at `/app/build/reports/tests/`
    - XML test result files at `/app/build/test-results/`
1. Locate the instrumented test results at
    - HTML test result files at `/app/build/reports/androidTests/connected/`
    - XML test result files at `/app/build/outputs/androidTest-results/connected/`
