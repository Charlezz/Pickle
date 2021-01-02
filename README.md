# Pickle - The fastest image picker for Android
## 🚧🚧Work in Progress🚧🚧
[ ![Download](https://api.bintray.com/packages/charlezz/Pickle/com.charlezz.pickle/images/download.svg) ](https://bintray.com/charlezz/Pickle/com.charlezz.pickle/)
<p>
<img src="https://github.com/Charlezz/Pickle/blob/main/pickle.jpg" width="200">
<br>
Designed based on Paging3.

## Installation
```bash
implementation 'com.charlezz:pickle:latest_version'
```
## How to use
1. Initilization
The launcher MUST be initialized before Lifecycle.State.STARTED
```bash
val launcher:ActivityResultLauncher<Config> = getPickle { mediaList:List<Media> ->
    //handle the result
}
```
2. Configure your Config if you want and call launch() function
```bash
launcher.launch(Config.default)
```
