<p align="center">
  <a href="https://http://www.android.com">
		<img src="https://img.shields.io/badge/android-21-green.svg?style=flat">
	</a>
	<a href="https://jitpack.io/#igormatyushkin014/TellMe-for-Android">
		<img src="https://jitpack.io/v/igormatyushkin014/TellMe-for-Android.svg">
	</a>
	<a href="https://tldrlegal.com/license/apache-license-2.0-(apache-2.0)">
		<img src="https://img.shields.io/badge/License-Apache 2.0-blue.svg?style=flat">
	</a>
</p>

## At a Glance

`TellMe` is a library that simplifies work with voice in Android.

## How to Get Started

Add `jitpack.io` repository to your project:

```javascript
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

Then add `TellMe` to dependencies list:

```javascript
dependencies {
    implementation 'com.github.igormatyushkin014:TellMe-for-Android:1.2.1'
}
```

## Requirements

* Android SDK 21 and later
* Android Studio 3.3 and later
* Kotlin 1.3 or later

## Usage

Use it from any activity, fragment or service:

```kotlin
tellMeIn(Locale.ENGLISH)
    .say("Hello")
```

Another example with chain of texts:

```kotlin
tellMeIn(Locale.ENGLISH)
    .say("Hello! How are you doing?")
    .say("What's up?")
    .say("Tell me something new.")
```

Want more flexibility? Add a listener:

```kotlin
tellMeIn(Locale.ENGLISH)
    .say("Hello! How are you doing?")
    .setOnSpeechListener(
        object : Speaker.OnSpeechListener {
            override fun onStartedSaying(text: String) {
                // Called when text is going to be pronounced
            }

            override fun onProgress(text: String, position: SpeechPosition) {
                val currentlyPronouncing = text.substring(position.start, position.start + position.length)
                // Called when a part of source text is going to be pronounced
            }

            override fun onFinishedSaying(text: String) {
                // Called when finished speaking
            }
        }
    )
```

Also, to make sure that all resources are released, call `releaseWhenFinish()` anywhere in the chain:

```kotlin
tellMeIn(Locale.ENGLISH)
    .say("Hello! How are you doing?")
    .say("What's up?")
    .say("Tell me something new.")
    .releaseWhenFinish()
```

Use this method when you don't need to use text-to-speech conversion frequently.

## License

`TellMe` is available under the Apache 2.0 license. See the [LICENSE](./LICENSE) file for more info.
