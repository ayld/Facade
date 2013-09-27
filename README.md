[![Build Status](https://travis-ci.org/ayld/Facade.png?branch=master)](https://travis-ci.org/ayld/Facade)

Facade
======

Facade tries to create a jar from all of the jars used by a project.
The facade jar should contain only classes the project actually uses, nothing else.

This is currently just a library, but it should evolve to a Maven plugin and perhaps a web project.

## Features

### Source Dependency Resolution

Facade can resolve the dependencies of .java source files. 
Currently like this:

```java
FacadeApi
  .buildWithDefaultConfig()
  .dependencies()
  .fromSource(SourceFile.fromFile(aFile));
```

### Binary Class Dependency Resolution

Facade can also resolve the dependencies of compiled binary .class files:

```java
FacadeApi
  .buildWithDefaultConfig()
  .dependencies()
  .fromClass(ClassFile.fromFile(aFile));
```

### Library minimization

This will try to find all the 'actual dependencies' that a set of sources use and package them in a Jar.

```java
final JarFile facadeJar = ApiBuilder
                            .buildWithDefaultConfig()
                            .compressDependencies(srcDir, libsDir);
```

### Update listeners

Facade can notify you for updates on what it is currently doing. For instanse if you want to get detailed info while 
resolving the dependencies of a binary class you can:

```java
ApiBuilder
  .buildWithDefaultConfig()
	.addListener(new Object() {
	
    @Subscribe
		public void receiveClassResolverUpdates(ClassResolverUpdate u) {
		  System.out.println(u);
		}
	})
	.compressDependencies(srcDir, libDir);
```



## Notes

Keep in mind that we're currently in very early alpha and the API changes constantly and can change dramatically :)
