# Krop
![License](https://img.shields.io/badge/license-MIT-blue.svg)
[![Build Status](https://travis-ci.org/avito-tech/krop.svg?branch=master)](https://travis-ci.org/avito-tech/krop) 
[![JitPack](https://jitpack.io/v/avito-tech/krop.svg)](https://jitpack.io/#avito-tech/krop)

The library is a small widget for image cropping in Instagram-like style.

![GifDemo](/art/krop-demo.gif)

## Gradle

Step 1. Add this in your root `build.gradle`

```
    allprojects {
        repositories {
            maven { url 'https://jitpack.io' }
        }
    }
```

Step 2. Add the dependency

```
    dependencies {
        compile 'com.github.avito-tech:krop:VERSION'
    }
```

If you like to stay on the bleeding edge, or use certain commit as your dependency, you can use the short commit hash or anyBranch-SNAPSHOT as the version

## Demo

Please see the demo app for library usage example.

### Usage:

Add KropView to your layout:

```xml
<com.avito.android.krop.KropView
    android:id="@+id/krop_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:krop_aspectX="1"
    app:krop_aspectY="1"
    app:krop_offset="24dp"
    app:krop_overlayColor="#aaffffff"/>
```

To set bitmap you need to crop, just use method:
```kotlin
kropView.setBitmap(bitmap)
```

### Obtaining result

To get cropped bitmap, use `getCroppedBitmap`:

```kotlin
val bitmap = kropView.getCroppedBitmap()
```

or get service object `BitmapTransformation` and crop bitmap manually when you need it:

```kotlin
val changes = kropView.getResultTransformation()
val result = bitmap.transformWith(changes)
```

### Listeners

You may listen for image update event (move, scale, rotation), by defining `TransformationListener`:

```kotlin
    interface TransformationListener {

        fun onUpdate(transformation: KropTransformation)

    }
```

### Basic View Configuration

* Set an initial crop area's aspect ratio:

in XML layout:
```java
app:krop_aspectX="4"
app:krop_aspectY="3"
```
or in code:
```
kropView.applyAspectRatio(aspectX = 4, aspectY = 3)
```

* Set overlay color:

in XML layout:
```java
app:krop_overlayColor="#aaffffff
```
or in code:
```
kropView.applyOverlayColor(color = 0xaaffffff)
```

* Set minimum crop region offset (px):

in XML layout:
```java
app:krop_offset="24dp"
```
or in code:
```
kropView.applyOffset(offset = 120)
```

* Set overlay style:

in XML layout:
```java
app:krop_shape="rect"
```
or in code:
```
kropView.applyOverlayShape(1)
```
All standart shapes are listed in OverlayShape class.

You can also create your own overlay, by inheriting OverlayView, and referencing it.

in XML layout:
```java
app:krop_overlay="@id/custom_overlay"
```
or in code:
```
kropView.applyOverlay(CustomOverlay())
```

### License

```
The MIT License (MIT)

Copyright (c) 2017 Avito Technology

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```