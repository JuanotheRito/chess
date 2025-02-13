# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Server Design Link

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo7vvorxLCaRLhhaHIwNyvIGoKwp2pQAByo4wOcMAgKkKAgA2MBVGRn6hlRbqdlhCpKiqWGdv+aYoTy2a5pg8kSVeab9KM6jAOS4xTAAolA3hpj4wRBtASAAF4oMs34aZ22Q9jAAAsTgAIxDn02mqLp9yGcZ0DlGZ06WTZdmiUy4lJpesHlIx2iOs6BK4Sy5Set6QYBkGIautKka0dGMCxsGiWyTFZxAiWiloMpCB5lBHYVT+VwDMRowgTWQazs2-ztiCpxORgfYDl5r73NWU7dY2zbjq2fRLpwq7eH4gReCg6B7gevjMMe6SZJgQ3MDBJTlBU0gGbuBn1AZzQtA+qhPt0049Wg-VsvJVYvTN86fKpjVQAUkkwAh9g7ch22+mhGKYXKKAFHlpIwOSYAldlcbaJRUX5YUlrwLx-EwEgABmxXTXOMAoAAHtCGiIxGJ3giV8ZwxqqWkhwKDcJkaPTrl7MRrjtHSFzFKGMz2gwMT3jDMjSA0EkJWvUlibJgD5QoTtdUNVVA2xadpb2dQTWDeeYAjYOvSQbr6nG5p3ksn5+l9EZJnBeZfphbZkxG8cFVHeUbmeb0Ds6XpVau0FMAhRZUDWd7i0rp4K0bpCtq7tCMAAOKjqye2nodZtsi1lRZ9dd32KOz3k+g71q7rEzfXOC5TP9NuM-KyOjhnsTIdC0MYSr2EpWJaXIxSaMS-IWNmoL7LlHABMCSTZN1j9lM07EdMC7bTqd1PwBD2zo9IyjOfabCB8z9RBXlJEFioDQ2ccT3WD07v4LP6Mr9HxVn1fygV+2s26pl3gBKYldtL6QqP0SBKAACS0h9LuV7AAZhck8E8mQDQVgmF8HQCBQANhwcBPBTw4HsVGHgvYMBGi+xNoUAOMB+yWx6BA3O0DYGjkQcgtBGCphYP1O1Ca+DCH8RIR1Mh7DRiULgn0BatDragOahpUobDQ6+XDgFN20cPapC9ssGA9DAYFCYUHIcUwfJOwjoFUyeiDE+zMEtZO65AjYB8FAbA3B4C6kyAAlI+0zw5GOvra8tQGgVyrno16Q4KGjmMR9dWU017NzmpNLhMjgJpP2DAEB0F9afwypkc+KBYRwF8SgAeWJf4j2xqfCeWUr6RVnjRBeS8iakyVuvamtNmk3w7nBVepV5A1JkALfCYASmwhKbI6+0UhZtL4svUmMyOIxE4ggXUHAwjyDnH06KAypLKn2flQ5ACh4I3GdxCpUzVmjDmTjeePivR+LgTAZIGRUgwDuAA2Rv966pgXhU4BakVF2yuNIlAsiIJ11NsEi2Fi+hxMkasPJDCijgvUVYrRLtbHu1CnHcKjifwxTMR5RF2L-K4p0THT2hLvZGLRXrDFBssWOxxZHNMehbSQjRBiYlDl-Zm0DqNXolj2VUs5eUblMBeXoTAI45cy1XEBEsFzBCyQYAACkIA8n8QEAhRDC7BOLqoyoVRKR3haHA6uKT0BDi8cANVUA4AQAQlAWYcDEEJIBcCL6Nd5zZLWP0R1zrXXupWAAdRYPAm6LQABCu4FBwAANJfC9Ug8oKD0FKPySyz+AArXVaApk6qUigPlg9yq1NnhMyeOVMYnPNLffGSyOlDNehvXp78YrAwPqM+mEypleo4A85tCzW2ExXiU95nz-GIK7qMeBtoelbybQzApncSkDquUWnkJSFCqHDdAaZJEYDRtjQZBNSbU2zCaT2idAA1SgJMdmpDkNxN10AiashCL8XQ3i9UGG4OAWyi7DBcT3UkfQMA-QfpAF+qA66P5btHBc31JYy21TUCpUFJ1Lg9B9XCnsLChy5vRS1NlYdJV4qFMAHlFb5UCrto5YVblLalnFdR52Uq6MMcrQqxleHQllCo5omjNL7H0sMURxhbGPIwG6JxjR1jtFR1pfo6TjjhMsoI1x8TPHaPAE2V6bZsHgCpNk12eFblUGKaSGJ1T1Ko7Ga2Tsizs1E7KtWgELwTquxelgMAbAXjCDxESAEguR1TXgsqBdK6N07rGDrn+dWhGmUxb3oMzm3NSmw1gsfOp7ojCi0yIyPQBhYRjrnnjEWuXYO8SMNoCrYhyoYbTHVsWIKAZgM0sY0lwrmGisI7kpVQA
