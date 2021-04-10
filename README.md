# Memo
Android processing and secured library for managing SharedPreferences as key-value elements persistence efficiently and structurally.

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

### 3. Injector
#### 3.1 Build MemoEntity Class
```java
/**
 * the entity generated will be named UserProfile_MemoEntity
 * it was annotated with @MemoEntity("UserProfile")
 *
 * the entity will be encrypted using the "G15y3aV9M8d" key
 * it was annotated with @EncryptEntity("G15y3aV9M8d")
 */
@MemoEntity("UserProfile")
@EncryptEntity("G15y3aV9M8d")
public class User
{

    /**
     * the default value will be "zeoflow"
     *
     * generated field will be named username
     *
     * this field is observable
     * it was annotated with @Observable
     */
    @KeyName("username")
    @Observable
    protected final String userUsername = "zeoflow";

    /**
     * generated field name will be login - lowerCamel
     *
     * this field will have its own onChangedListener
     * it was annotated with @Listener
     */
    @Listener
    protected final boolean login = false;

    /* the default value will be 1 */
    @KeyName("views")
    protected final int viewsCount = 1;

    /* the default value will be null */
    @KeyName("userinfo")
    protected PrivateInfo privateInfo;

    /**
     * preference putter function for userUsername.
     *
     * @param userUsername function in
     *
     * @return function out
     */
    @MemoFunction("username")
    public String putUserUsernameFunction(String userUsername)
    {
        return "Hello, " + userUsername;
    }

    /**
     * preference getter function for userUsername.
     *
     * @param userUsername function in
     *
     * @return function out
     */
    @MemoFunction("username")
    public String getUserUsernameFunction(String userUsername)
    {
        return userUsername + "!!!";
    }

    /**
     * preference putter function example for visitCount's auto increment.
     *
     * @param count function in
     *
     * @return function out
     */
    @MemoFunction("views")
    public int putVisitCountFunction(int count)
    {
        return ++count;
    }

    /**
     * preference getter compound function for following fields.
     *
     * Params declared inside @MemoCompoundFunction's annotation
     * @param username function in
     * @param views function in
     *
     * @return $username $views
     */
    @MemoCompoundFunction(values = {"username", "views"})
    public String getUserViews(String username, int views)
    {
        return username + " " + views;
    }

    /**
     * preference getter compound function for following fields.
     *
     * Params declared inside @MemoCompoundFunction's annotation
     * @param userinfo function in
     *
     * @return $first_name $last_name
     */
    @MemoCompoundFunction(values = {"userinfo"})
    public String getFullName(PrivateInfo userinfo)
    {
        return userinfo.getFirstName() + " " + userinfo.getLastName();
    }

    /**
     * preference getter compound function for following fields.
     *
     * Params declared inside @MemoCompoundFunction's annotation
     * @param userinfo function in
     * @param views function in
     *
     * @return $first_name $last_name, views count $views
     */
    @MemoCompoundFunction(values = {"userinfo", "views"})
    public String getFullNameAndViews(PrivateInfo userinfo, int views)
    {
        return userinfo.getFirstName() + " " + userinfo.getLastName() + ", views count: " + views;
    }

}
```

## License
    Copyright (C) 2021 ZeoFlow S.R.L.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
      http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## 🏆 Contributors 🏆

<!-- ZEOBOT-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<p float="left">
<a href="docs/contributors.md#pushpin-teodor-g-teodorhmx1"><img width="100" src="https://avatars.githubusercontent.com/u/22307006?v=4" hspace=5 title='Teodor G. (@TeodorHMX1) - click for details about the contributions'></a>
</p>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ZEOBOT-LIST:END -->