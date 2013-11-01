[![Build Status](https://travis-ci.org/ayld/Facade.png?branch=master)](https://travis-ci.org/ayld/Facade)

Facade
======

Facade tries to create a jar from all of the jars used by a project.
The facade jar should contain only classes the project actually uses, nothing else.

This is currently just a library, but it should [evolve to a Maven plugin](https://github.com/amaranthius/facade-maven) and perhaps a web project.

## Features

### Source Dependency Resolution

Facade can resolve the dependencies of .java source files. 
Currently like this:

```java
final Set<ClassName> dependencies = Dependencies
    .ofSource(SourceFile.fromFilepath("/abs/path/to/Source.java"))
    .set();
```

### Binary Class Dependency Resolution

Facade can also resolve the dependencies of compiled binary .class files:

```java
final Set<ClassName> dependencies = Dependencies
    .ofClass(ClassFile.fromFilepath("/abs/path/to/Class.class"))
    .set();
```

The `Dependencies` API can also work with the classpath or with Core API `File` objects (no streams yet though).
For more info on the `Dependencies` API have [a look at the wiki](https://github.com/ayld/Facade/wiki/Dependencies-API).

### Library minimization

This will try to find all the 'actual dependencies' that a set of sources use, package them in a Jar and return it.

```java
final JarFile outJar = LibraryMinimizer
    .forSourcesAt("/abs/path/to/src/dir")
    .withLibs("/abs/path/to/libs") // this can also be a Maven ~/.m2/repository
    .getFile();
```

You can also set the output dir for the minimizer, have a look at the wiki for info.

### Component Events

Facade can notify you for updates on what it is currently doing. For instanse if you want to get detailed info while 
resolving the dependencies of a binary class you can:

```java
ListenerRegistrar.listeners(new Object() {
			
    @Subscribe
	public void listenOnStart(ClassDependencyResolutionStartEvent e) {
	    // this will be called when the resolution starts
	}
			
	@Subscribe
	public void listenOnEnd(ClassDependencyResolutionEndEvent e) {
	    // this will be called when the resolution ends
	}
}).register();
```

There is a whole hierarchy of events you can listen to, there is [a wiki page](https://github.com/ayld/Facade/wiki/Component-Events-and-Listeners) on this also.

## Usage

In order to use the library you can either:

 * [Download the latest binary .jar](https://github.com/ayld/Facade/releases/tag/v0.5-alpha.3), 
in which case you will also need the dependencies. You can either find them [in the POM](https://github.com/ayld/Facade/blob/master/pom.xml),
or download them from [the dependencies project](https://github.com/ayld/facade-dependencies).
 * Build [the latest tag](https://github.com/ayld/Facade/tree/v0.5-alpha.1) with [Maven 3.x](http://maven.apache.org/). Just
clone it and run `mvn clean install` in your local copy. This way Maven will get the dependencies for you.

You can also build the master branch (on your own risk) in the same way you build the latest tag.


## Notes

Keep in mind that we're currently in very early alpha and the API changes constantly and can change dramatically :)
Also wildcard imports in source files like `import com.something.*;` are currently not supported. So calling source
dependency resolution on such a file or on a set containing one will result in an exception.
