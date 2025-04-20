# Ctrl-Fallback
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Library to quick search for methodhandles if you are not sure that the method will be in your application at launch 
(mostly for internal jdk api)
***
`com.ydo4ki.ctrlf.MethodSequence` and `MethodFinder` are classes that helps you to get MethodHandle for the first found method in the sequence

To create `MethodSequence` use `MethodSequence::of(...)`<br>
It takes `MethodType` or `Class<?>` of field type and method used to search (`SearchMode`)

Every search mode represents one of "find" methods in MethodHandles.Lookup:

| SearchMode                    | Linked with                              |                                        Takes arguments (signature) |
|:------------------------------|:-----------------------------------------|-------------------------------------------------------------------:|
| `SearchMode.findStatic`       | `MethodHandles.Lookup::findStatic`       |                        `(String method_owner, String method_name)` |
| `SearchMode.findVirtual`      | `MethodHandles.Lookup::findVirtual`      |                       `(String method_owner, String method_owner)` |
| `SearchMode.bind`             | `MethodHandles.Lookup::bind`             |                           `(Object receiver, String method_owner)` |
| `SearchMode.findGetter`       | `MethodHandles.Lookup::findGetter`       |                          `(String field_owner, String field_name)` |
| `SearchMode.findSetter`       | `MethodHandles.Lookup::findSetter`       |                          `(String field_owner, String field_name)` |
| `SearchMode.findStaticGetter` | `MethodHandles.Lookup::findStaticGetter` |                          `(String field_owner, String field_name)` |
| `SearchMode.findStaticSetter` | `MethodHandles.Lookup::findStaticSetter` |                          `(String field_owner, String field_name)` |
| `SearchMode.findConstructor`  | `MethodHandles.Lookup::findConstructor`  |                                       `(String constructor_owner)` |
| `SearchMode.findSpecial`      | `MethodHandles.Lookup::findSpecial`      | `(String method_owner, String method_name, String special_caller)` |

Example:

```java
import java.lang.invoke.MethodHandles;

private static final MethodHandle caller = MethodSequence.of(MethodHandles.lookup() /* lookup to use */, MethodType.methodType(Class.class, int.class), SearchMode.findStatic)
		.find("sun.reflect.Reflection","getCallerClass") // trying to find internal jdk method
		.orElse("me.random.Java9StackWalkerFallback","getCallerClass9") // if internal method is absent or access denied, try to load class from java 9 to use faster implementation with stackwalker
		// you can add as many .orElse(...) as you want here
		.fallback("me.random.CallersSlow","getCallerClass") // if all previous methods not found, use slow fallback that works everywhere
		.methodHandle(); // will throw exception if fallback was not loaded somehow nothing found
```
Also, if some parameters are repeated more often than others, you can set a default value for them:

```java
private static final MethodHandle caller = MethodSequence.of(MethodType.methodType(Class.class, int.class), SearchMode.findStatic).withName("getCallerClass")
		.find("sun.reflect.Reflection") // name = "getCallerClass"
		.orElse("me.random.Java9StackWalkerFallback", "getCallerClass9") // override default name value
		.fallback("me.random.CallersSlow") // name = "getCallerClass"
		.methodHandle();
```

*For more details see javadoc*


## Installation

### Maven

```xml
<dependency>
    <groupId>com.ydo4ki</groupId>
    <artifactId>Ctrl-Fallback</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.ydo4ki:Ctrl-Fallback:1.0.0'
```
