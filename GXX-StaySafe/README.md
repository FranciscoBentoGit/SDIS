# StaySafe

Distributed Systems 2020-2021, 1st semester project


## Authors

 
**Group G11**

### Code identification

In all the source files (including POMs), please replace __G11__ with your group identifier.  
The group identifier is composed of a G and the gropu number - always with two digits.  
This change is important for code dependency management, to make sure that your code runs using the correct components and not someone else's.

### Team members
 

| Number | Name              | User                                  | Email                                 |
| -------|-------------------|---------------------------------------|---------------------------------------|
| 93581  | Francisco Bento   | <https://github.com/FranciscoBentoGit>| <francisco.bento@tecnico.ulisboa.pt>  |
| 93588  | João Lopes        | <https://github.com/Joao-Pedro-Lopes> | <joaopedrolopes00@tecnico.ulisboa.pt> |


### Task leaders


The group has made the choice of not spliting tasks, but instead doing the project side by side and every choice was made by both.


## Getting Started

The overall system is composed of multiple modules.
The main server is the _dgs_.
The clients are the _sniffer_, the _journalist_ and the _researcher_.

See the [project statement](https://github.com/tecnico-distsys/StaySafe/blob/main/part1.md) for a full description of the domain and the system.

### Prerequisites

Java Developer Kit 11 is required running on Linux, Windows or Mac.
Maven 3 is also required.

To confirm that you have them installed, open a terminal and type:

```
javac -version

mvn -version
```

### Installing

To compile and install all modules:

```
mvn clean install -DskipTests
```

The integration tests are skipped because they require the servers to be running.


## Built With

* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [gRPC](https://grpc.io/) - RPC framework


## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Main Concept of the Project

The server has 2 main variables which he operates with : a ConcurrentHashMap to store different sniffers and their respective addresses and CopyOnWriteArrayList that procede to store observations with the type "ObservationsData", a custom class created to contain <snifferName>,<infection>,<id>,<timeIn>,<timeOut>,<timestamp>.

Specific clients, like sniffer,journalist or researcher interact with dgs_server through : specific client calls a certain function from a father client that represents all clients,then he calls the associated function(according to objective) of the frontend ( bridge between dgs_server and father client),which creates the stub to interact with dgs_server, through his implementation.

Each command and usage of each client will be available in the respective README.md folder.



