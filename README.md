# RxWorkManagerObservers

This library contains a set of extension functions to the WorkManager and LiveData allowing to observe enqueued work in a reactive manner.

## Download

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
 
```gradle
dependencies {
      implementation 'com.github.PaulinaSadowska:RxWorkManagerObservers:1.0.0'
}
```

The library does not depend on work-rxjava2. It works with any type of worker that can be enqueued by the WorkManager.

## WorkManager extensions

### Observing the Data by id:

#### Observing the WorkInfo by id:

### Observing the Data by tag

### Unique work

## LiveData extensions

## Threading
The Observable returned from each function from this library **have to be subscribed on the main thread** (because there is ``LiveData.observeForever`` called under the hood). The actual work, will still be executed on the background thread specified by the WorkManager.

## More info
More about this library and the WorkManager you can read in my article: 

## License
```
Copyright 2019 Paulina Sadowska

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
