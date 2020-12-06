# StaySafe

Distributed Systems 2020-2021, 1st semester project


## Authors

 
**Group G11**



## Team members
 

| Number | Name              | User                                  | Email                                 |
| -------|-------------------|---------------------------------------|---------------------------------------|
| 93581  | Francisco Bento   | <https://github.com/FranciscoBentoGit>| <francisco.bento@tecnico.ulisboa.pt>  |
| 93588  | João Lopes        | <https://github.com/Joao-Pedro-Lopes> | <joaopedrolopes00@tecnico.ulisboa.pt> |

![](https://avatars2.githubusercontent.com/u/71926361?s=460&u=d492ef164d3c4372029fb291e8a62ba9f1df782a&v=4 | heigth=150)
![](https://avatars1.githubusercontent.com/u/56592131?s=460&u=6d4865457f240460a9587622c1b1a286f6fd12d8&v=4 | heigth=150) 


## First part improvements

Note: our group has not been able to get links for commits since we worked on a private repository and no one else will be able to see what's inside.

1. Replaced the way we split the answer in order to get responses with more than 1 field, now we take advantage of the fuctions created by protobuf for each response.(This can be seen in individual and aggregate probabilities on both journalistApp and researcherApp).
2. ReplicaId is now passed by every client function.
3. Exceptions being caught now have more information.



## Fault tolerance model



## Solution for fault tolerance



## Replication protocol

In order to answer to **weak consistency, client only needs to contact one replica to be able to do an update, periodic propagation and consistency between reads on the same client**,
we decided to implement a **gossip protocol version with a consensus update, needing 2f+1 replica managers to handle faults** .
Client chooses which of 3 replica managers to contact (1,2 or 3) and if one is not given, client will contact a random one.
After choosing the replica the client is free to do any operation(between the boundaries designed for each client), in case of an update message,**this will be done immediately localy(contacting replica manager)** and after **some time (propagation function is running every 30 secs) the remaining replicas will be on the same state if the update message is successful**.
**Consistency between reads** is guarenteed by having a **previous timestamp for each client( defined on frontend)** and when a response from a reading message arrives, the **previous timestamp will be compared with the received timestamp**, the awnser selected will be selected between the **previous message stored(lastest read for that function) if the receiving timestamp is lower than the previous one** or the awnser will be the **response that replica manager returned and backup read will be updated**.

To explain how the propagation works, lets start with Foo.java and why the class was created.
In order to pass values into an annonymous runnable, the variables needed to be final(values cannot change). That implied **creating a Foo object, passing by the zkNaming, impl and path**, so we can later on use them on the function **tick()**,defined in Foo.java.
Tick() uses the following important attributes :

1. parent - defines the path for parentNode.

2. replicaCollection - with the receiving zkNaming object, we are able to get which nodes does the parentNode own.

3. list - List of operations(update messages) from the replica we are working on.

4. impl - Each implementation now contains ExecutedList, LogList and valueTimestamp, being different for every relica manager.

5. valueTs - This replica timestamp.

6. path -This replica path

Taking this into consideration, tick() starts by **getting the timestamp from who called the function**. Then it **splits his path and runs all zkNaming record list to get all existent nodes**.
For **every nodes path that differs from his own**, he creates a **communication channel(creating an ServersFrontend object) with him and asks him what's his current timestamp**( function update() defined in Servers Frontend).
Now it **iterates all over his logList(containing Operations objects, that carry operation identifier, request type,and the request itself)** and for every operation that is valid, which requires this **value timestamp being bigger than the others replica timestamp** and the **operation identifier being bigger than the value timestamp columns that corresponds to this replica ts(replica1,replica2,replica3)**, it calls the required DgsServiceImpl function taking the **request as an argument** (JoinRequest/ReportRquest/ClearRequest).
Into DgsServiceImpl class, for every update function(join,report and clear), we compare this **request properties with all executed so far, to detect duplicate updates**.If the request is not duplicate,**value timestamp is increased by one on the replicaId column**, a **new operation is created and added to both lists, executed and log list**.
With the specific case of clear(), first we need to **clear both lists before adding a new clear operation**.

## Implementation options 

1. Each client frontend function now returns the value timestamp from a replica manager.

2. Periodic propagation has been handled by creating a frontend for replica managers, so they can handle communication channels between them. Each replica manager will be ran by initiatting a different terminal on /dgs-server , typing mvn compile exec:java(basically running a serverApp) and giving which number between 1 and 3 to initiatte.This serverApp now has a runnable function to run every 30 seconds,
calling a **Foo.java function(Foo is designed to handle propagation cicle) called tick()**. Since we can't initiatte replicas at the same time, this guarentees that propagation is asynchronous between them.

3. **ExecutedList, LogList and valueTimestamp have been added to DgsServiceImplementation** in order to help with periodic propagation.

4. Period for propagation is easily changed by changing the value defined as period on DgsServer.app.