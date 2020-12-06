

# Demo


## Using files text


Text files will be available here in order to either trying out data redirection for sniffer client ,using < ../demo/[fileName] or to call operation init, which allows to define startup parameters and fill the observations field with examples inside data.txt.
To able to call function init, you might want to type help after  iniating the specific client, so you can learn which arguments are needed and the specific order.
On [data.txt] argument, you will need to type :

```
 < ../demo/<fileName>
```
## Initiatting zooKeeper

Make sure to have zookeeper installed. Go inside your folder instalation for zookeeper at ../zookeeper/bin and type :

```
-- ./zkServer.sh start
```
A message should appear staying server started if all went alright.


# Utilization Guide

**NOTE** : Same join operations do not increase the number of operations but they are valid.


## Example for client : Sniffer


**1 step** : Make sure you are at the main folder of the project, G11-STAYSAFE.

**2 step** : Proceed to type :

```
-- mvn clean install -DskipTests
```
**3 step** : Open 5 terminals and decide which 3 terminals will serve to initiate each replica manager, which will initiate zooKeeper and which will play the sniffer role.

**4 step** : Initiate each replica managers , by getting inside the G11/dgs-server and typing :


```
-- mvn compile exec:java 
```
**5 step** : Inside each replica terminal, type a different replica to start, going from 1 to 3.


**6 step** : Get inside the G11/sniffer folder and type (you can choose to which replica to connect, if not given it will be random) : 

```
 ./target/appassembler/bin/sniffer localhost 2181 <snifferName> <address> <replicaId to contact>(optional) < <pathToFilename.txt>(optional)
```

**7 step** : If you new, start by typing help to get a feeling of the available commands to this specific client.

Lets suppose we want to register 2 observations for this sniffer.

**8 step** : type :

```
    <infectionState>,<citizenId>,<timeOfDetectingEntrance>,<timeOfDetectingExit>
```

Example : infetado,123456789,2000-11-12 12:35:43,2000-11-12 13:17:48.

**Side note** : as soon as you press enter on the first observation, the system will detect that you have entered on "observation mode" and will allow you to register as many observations as you wish, accumulating them until you press Enter or type exitSniffer, printing out the result of every attempt .


## Example for client : Journalist

**1 step** : Make sure you are at the main folder of the project, G11-STAYSAFE.

**2 step** : Proceed to type :

```
-- mvn clean install -DskipTests
```
**3 step** : Open 5 terminals and decide which 3 terminals will serve to initiate each replica manager, which will initiate zooKeeper and which will play the sniffer role.

**4 step** : Initiate each replica managers , by getting inside the G11/dgs-server and typing :


```
-- mvn compile exec:java 
```
**5 step** : Inside each replica terminal, type a different replica to start, going from 1 to 3.


**6 step** : On Journalist terminal ,get inside the G11/journalist folder and type (you can choose to which replica to connect, if not given it will be random) : 

```
    ./target/appassembler/bin/journalist localhost 2181 <replicaId to contact>(optional)
```

**7 step** : Either you already did the sniffer guide and you have got a small data to work with within the server or you want register some observations to a specific sniffer and maybe not initialized yet. In order to go for the second option, type :

```
    init <pathToFileData.txt> <snifferName> <address>
```

**8 step** : Assuming that you have entered a non existent sniffer or a existent one giving it the right address that he already has, you will register the observations sucessfully.

**9 step** : Lets get some statistics about our data, type :

```
    mean_dev
```
 
To get the mean and standard deviation values for all non-infected citizens, in result line respectfully.


## Example for client : Researcher


**1 step** : Make sure you are at the main folder of the project, G11-STAYSAFE.

**2 step** : Proceed to type :

```
-- mvn clean install -DskipTests
```
**3 step** : Open 5 terminals and decide which 3 terminals will serve to initiate each replica manager, which will initiate zooKeeper and which will play the sniffer role.

**4 step** : Initiate each replica managers , by getting inside the G11/dgs-server and typing :


```
-- mvn compile exec:java 
```
**5 step** : Inside each replica terminal, type a different replica to start, going from 1 to 3.


**6 step** : On Researcher terminal ,get inside the G11/researcher folder and type (you can choose to which replica to connect, if not given it will be random) : 

```
    ./target/appassembler/bin/researcher localhost 2181 <replicaId to contact>(optional)
```

**7 step** : Either you already did the sniffer guide and you have got a small data to work with within the server or you want register some observations to a specific sniffer and maybe not initialized yet. In order to go for the second option, type :

```
    init <pathToFileData.txt> <snifferName> <address>
```

**8 step** : Assuming that you have entered a non existent sniffer or a existent one giving it the right address that he already has, you will register the observations sucessfully.

**9 step** : Lets get some statistics about our data, type :

```
    mean_dev
```

To get the mean and standard deviation values for all non-infected citizens, in result line respectfully.

**10 step** : As you are a researcher, you have some extra permission and so you can get a statistic for a single target. Open a data.text inside /demo folder and look for a specific citizen ID, copy it out and type :

``` 
    single_prob <id>
```

This will give you the probability of a specific citizen being infected, 1.00 if he already is infected or a value between 0 and 1,according to our screet formula that uses the biggest time value of being with an infected citizen.

**11 step** : Imagine you wanted to test a different text file, just go ahead and type :

```
    clear
``` 
This will clear all server information about observations, so you can get different statistics.


## Example of a normal functionality

**1 step** : Merge sniffer guide with researcher guide(this will need 6 terminal in total).Make sure you connect both clients to different replicas, lets say sniffer-1 and researcher-2.

**2 step** : After performing some changing operations on replica 1, try to get some stats on the researcher. If you were too quick, probably stats won't have any data. Wait until you see propagation messages in the replica 2 terminal.Try again to get some stats, it should have updated information now.

**3 step** : Close the researcher connection to replica 2 and now do it for replica 3. Compare stat results with the ones from the previous step. They should be the same.

**4 step** : Try to clear on of these clients and pay attention to timestamps transmitted.Timestamp will be increased because it's an update operation but if you try stats again they wont work because all 3 replicas have been cleared.

**5 step** : Finally, make init on the client terminal, you will see that the timestamp will be increased by 8(1 join + 7 reports) and on the next propagation both 3 replicas will all their timestamps increased by 8 and all with the same data.

## Example of a fault tolerance

**1 step** : Do the sniffer guide.

**2 step** : Now kill the replica manager which the sniffer joined. To be able to do this,  discover the Process ID and open a new Terminal and type :
```
    kill -9 <PID>
``` 
**3 step** : Now observe the messages that pop up on the sniffer terminal. It should be something like this (in this scenario assume that sniffer joined initially replica 1 and X meaning the number of update messages done so far):

Caught exception with description: io exception when trying to contact replica 1 at localhost:8081

Trying to contact replica 2 at localhost:8082...

Frontend received answer with TS{X,0,0}

**Note**: in this case the join operation was already done, so the number of operation was not increased, that's why we dont see the Frontend with TS{X,1,0}.