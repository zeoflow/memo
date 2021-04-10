<!--docs:
title: "Getting Started"
layout: landing
section: docs
path: /docs/getting-started/
-->

### 1. Depend on our library

Memo Library is available through Maven Repository.
To use it:

1.  Open the `build.gradle` file for your application.
2.  Make sure that the `repositories` section includes Maven Repository
    `mavenCentral()`. For example:
```groovy
  allprojects {
    repositories {
      mavenCentral()
    }
  }
```

3.  Add the library to the `dependencies` section:
```groovy
dependencies {
    // ...

    // parcelled version
    def memo_version = "1.0.0"

    // Memo Library
    implementation("com.zeoflow:memo:$parcelled_version")
    // Required if you want to use the injector
    implementation("com.zeoflow:memo-annotation:$memo_version")
    annotationProcessor("com.zeoflow:memo-compiler:$memo_version")
    // For kotlin projects use kapt instead of annotationProcessor
    kapt("com.zeoflow:memo-compiler:$memo_version")

    // ...
}
```

Visit [MVN Repository](https://mvnrepository.com/artifact/com.zeoflow/memo)
to find the latest version of the library.

## Contributors

Memo welcomes contributions from the community. Check out our [contributing guidelines](contributing.md)
before getting started.
