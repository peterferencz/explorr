# Java Archive Explorer & UML Visualizer

`explorr` is a lightweight command-line tool for inspecting Java archives (`.jar` files) and generating UML-style representations of their internal structure.

## Features

- Inspect JAR file contents
- Discover classes and interfaces
- Extract inheritance hierarchies
- Generate UML class diagrams
- Lightweight CLI interface

### Prerequisites
- Java 11+
- Maven 3+

### Build

```bash
mvn clean package
```

## Usage

Analyze a JAR file:

```bash
java -jar explorr.jar my-library.jar
```

Generate UML output:

```bash
java -jar explorr.jar my-library.jar --uml
```
