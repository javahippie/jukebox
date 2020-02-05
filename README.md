![Java CI](https://github.com/javahippie/jukebox/workflows/Java%20CI/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/net.javahippie.jukebox/processor.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.javahippie.jukebox%22%20AND%20a:%22processor%22)

# Jukebox

_"Oh, let that jukebox keep on playin'_ \
 _Let that record roll around"_ - Carl Perkins

## Usage
This is a annotation processor to generate builder classes for the Java 14 preview feature records (JEP 359). You can get it from Maven Central:

```xml
<dependency>
    <groupId>net.javahippie.jukebox</groupId>
    <artifactId>processor</artifactId>
    <version>0.1</version>
</dependency>
``` 

Include the dependency and reference the class `net.javahippie.jukebox.processor.RecordBuilder` in the Maven Compiler Plugin like this:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <compilerArgs>
            <arg>--enable-preview</arg>
        </compilerArgs>
        <source>14</source>
        <target>14</target>
        <encoding>UTF-8</encoding>
        <generatedSourcesDirectory>${project.build.directory}/generated-sources/</generatedSourcesDirectory>
        <annotationProcessors>
            <annotationProcessor>net.javahippie.jukebox.processor.RecordBuilder</annotationProcessor>
        </annotationProcessors>
    </configuration>
</plugin>
``` 

After doing this, the library will generate a builder class for any record annotated with the annotation `net.javahippie.jukebox.annotation.Builder`.


## Intention

I had this idea when I compared Java records to Kotlins data classes and realized, that creating Java records with many 
record components would get confusing to the reader rather soon. If we would like to use records to represent a game between two people, the definitions would look like this:

```java
public record Person(
        String firstName,
        String lastName,
        LocalDate birthDate,
        int heightInCentimeters){}

public record Game(
        Person player1,
        Person player2,
        int pointsPlayer1,
        int pointsPlayer2){}
```

If we create an instance of a Game with both participants, this would be the only way to do it:

```java
new Game(new Person("Rüdiger", "Behrens", LocalDate.of(1982, 3, 17), 186)),
         new Person("Frank", "Meier", LocalDate.of(1990, 2, 2), 170)),
         10,
         20);
```

This would be more readable if we used named variables instead of literals, but it is hard to comprehend, what the meaning of each value is. 
As we do not have named parameters in Java, as we have in Kotlin, the builder pattern might help here:

```java
GameBuilder.builder()
            player1(PersonBuilder.builder()
                    firstName("Rüdiger")
                    lastName("Behrens")
                    birthDate(LocalDate.of(1982, 3, 17))
                    heightInCentimeters(186)
                    .build())
            Player2(PersonBuilder.builder()
                    firstName("Frank")
                    lastName("Meier")
                    birthDate(LocalDate.of(1990, 2, 2))
                    heightInCentimeters(170)
                    .build())
            pointsPlayer1(10)
            pointsPlayer2(20)
            .build();
```

Luckily, we are able to access the records and its components in a structured way, which enables us 
to implement an annotation processor, which automatically generates those builders for us. If any record in our project is
annotated with `@net.javahippie.recordbuilders.annotation.Builder`, the annotation processor will pick it up and generate a builder class in the following style:
```java
@Builder
public record Person(
        String firstName,
        String lastName,
        LocalDate birthDate,
        int heightInCentimeters){}

@Builder
public record Game(
        Person player1,
        Person player2,
        int pointsPlayer1,
        int pointsPlayer2){}
```


```java
package net.javahippie.jukebox.testtool;

public class PersonBuilder {

   private java.lang.String firstName;
   private java.lang.String lastName;
   private java.time.LocalDate birthDate;
   private int heightInCentimeters;

    public static PersonBuilder builder() {
        return new PersonBuilder();
    }

    public Person build() {
        return new Person(firstName, lastName, birthDate, heightInCentimeters);
    }

    public PersonBuilder firstName(java.lang.String firstName) {
        this.firstName = firstName;
        return this;
    }

    public PersonBuilder lastName(java.lang.String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PersonBuilder birthDate(java.time.LocalDate birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public PersonBuilder heightInCentimeters(int heightInCentimeters) {
        this.heightInCentimeters = heightInCentimeters;
        return this;
    }

}
```

If the Maven multimodule project is built with `mvn clean package`, the example application can be run with the command `java --enable-preview -jar testtool/target/testtool-1.0-SNAPSHOT.jar` from its root
