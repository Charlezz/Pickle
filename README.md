# Pickle - The fastest image picker for Android
[ ![Download](https://api.bintray.com/packages/charlezz/Pickle/com.charlezz.pickle/images/download.svg) ](https://bintray.com/charlezz/Pickle/com.charlezz.pickle/)

<p>
<img src="https://github.com/Charlezz/Pickle/blob/main/pickle.jpg" width="200">
<br>

Designed based on Paging3.
    
## Installation
### 1. Add dependency

First of all, add the repository to project's level `build.gradle`

```groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

 Add this line to `build.gradle` for your application module

```groovy
dependencies{
    implementation 'com.charlezz:pickle:latest_version'
}
```
### 2. Enable databinding

 Add this line to `build.gradle` for your application module

```groovy
android{
    buildFeatures.dataBinding = true
}
```

For the reason why Pickle uses dataBinding library, you may want to set dataBinding in your app `build.gradle` otherwise you will see the error below.

`java.lang.NoClassDefFoundError: Failed resolution of: Landroidx/databinding/DataBinderMapperImpl;`

## How to use

### 1. Initilization

The launcher MUST be initialized before Lifecycle.State.STARTED

```kotlin
private val launcher = Pickle.register(this, object:Pickle.Callback{
    override fun onResult(result: ArrayList<Media>) {
        adapter.setImages(result)
    }
})
```
### 2. Configure your Config if you want and call launch() function

```kotlin
launcher.launch(Config.getDefault())
```

## Configuration

Customize a config if you want

```kotlin
val config = Config.getDefault().apply {  
    //add whatever you want below
    debugMode = false // this flag disables printing log from Timber
}
```

You may want to use PickleSingle when user can pick only an Image or a Video.

```kotlin
// init
private val singleLauncher = PickleSingle.register(this, object:PickleSingle.Callback{
    override fun onResult(media: Media?) {
        // do something with a media
    }
})
...
// Launch Pickle with SingleConfig
singleLauncher.launch(Config.getDefault())
```

## For Java User

It's similar to what Kotlin does.

```java
private Pickle pickle = Pickle.register(this, result -> {
    // ...
});

pickle.launch(Config.getDefault())
```

## License

```text
Copyright 2021 Charlezz

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
