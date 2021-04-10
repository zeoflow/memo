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

    // declare memo version
    def memo_version = "1.0.0"

    // Memo Library
    implementation("com.zeoflow:memo:$memo_version")
    // Required if you want to use the injector
    implementation("com.zeoflow:memo-annotation:$memo_version")
    annotationProcessor("com.zeoflow:memo-compiler:$memo_version")
    // For kotlin projects use kapt instead of annotationProcessor
    kapt("com.zeoflow:memo-compiler:$memo_version")

    // ...
}
```

### 2. Usage
#### 2.1 Initialize
With Context
```java
Memo.init(context).build();
```
Without Context (the context is retrieved from the ContextProvider at runtime)
```java
Memo.init().build();
```
#### 2.2 Library Calls
Save any type (Any object, primitives, lists, sets, maps ...)
```java
Memo.put(key, T);
```
Get the original value with the original type
```java
T value = Memo.get(key);
```
Delete any entry
```java
Memo.delete(key);
```
Check if any key exists
```java
Memo.contains(key);
```
Check total entry count
```java
Memo.count();
```
Delete everything
```java
Memo.deleteAll();
```

#### 2.3 Customize the Library at initialisation
- Everything is pluggable, therefore you can change any layer with your custom implementation.
- NoEncryption implementation is provided out of box If you want to disable crypto.
```java
Memo.init(context)
  .setEncryption(new NoEncryption())
  // encryption_string is used to encrypt the data and is required
  .setEncryption(new ConcealEncryption(encryption_string))
  .setLogInterceptor(new MyLogInterceptor())
  .setConverter(new MyConverter())
  .setParser(new MyParser())
  .setStorage(new MyStorage())
  .build();
```

Visit [MVN Repository](https://mvnrepository.com/artifact/com.zeoflow/memo)
to find the latest version of the library.

## Contributors

Memo welcomes contributions from the community. Check out our [contributing guidelines](contributing.md)
before getting started.
