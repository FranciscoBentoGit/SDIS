

# Demo


## Using files text


Text files will be available here in order to either trying out data redirection for sniffer client ,using < ../demo/<fileName> or to call operation init, which allows to define startup parameters and fill the observations field with examples inside data.txt.
To able to call function init, you might want to type help after  iniating the specific client, so you can learn which arguments are needed and the specific order.
On <data.txt> argument, you will need to type < ../demo/<fileName>.


## Utilization Guide


### Example for client:Sniffer


First step : Make sure you are at the main folder of the project, G11-STAYSAFE.

Second step : Proceed to type -- mvn install -DskipTests.

Third step : Open another terminal and decide which terminal will play the server and sniffer role, getting inside on each terminal in the specific folder.

Fourth step : Initiate server, typing -- mvn compile exec:java , see if starts up.

Fifth step : Initiate sniffer, typing -- ./target/appassembler/bin/sniffer localhost 8080 <snifferName> <address> (optional) < <data.txt>

Sixth step : If you new, start by typing help to get a feeling of the available commands to this specific client.

Lets suppose we want to register 2 observations for this sniffer.

Seventh step : type -- <infectionState>,<citizenId>,<timeOfDetectingEntrance>,<timeOfDetectingExit>.
Example : infetado,123456789,12-11-2000 12:35:43,12-11-2000 13:17:48.
Side note : as soon as you press enter on the first observation, the system will detecting that you have entered on "observation mode" and will allow you to register as many observations as you wish, accumulating them until you press Enter or type exitSniffer, printing out the result of every attempt .


### Example for client:Journalist


First step : Make sure you are at the main folder of the project, G11-STAYSAFE.

Second step : Proceed to type -- mvn install -DskipTests.

Third step : Open another terminal and decide which terminal will play the server and journalist role, getting inside on each terminal in the specific folder.

Fourth step : Initiate server, typing -- mvn compile exec:java , see if starts up.

Fifth step : Initiate journalist, typing -- ./target/appassembler/bin/journalist localhost 8080

Sixth step : Either you already did the sniffer guide and you have got a small data to work with within the server or you want register some observations to a specific sniffer and maybe not initialized yet. In order to go for the second option, type -- init <pathToFileData.txt> <snifferName> <address>.

Seventh step : Assuming that you have entered a non existent sniffer or a existent one giving it the right address that he already has, you will register the observations sucessfully.

Eigth step : Lets get some statistics about our data, type -- mean_dev to get the mean and standard deviation values for all non-infected citizens, in result line respectfully.


### Example for client:Researcher

First step : Make sure you are at the main folder of the project, G11-STAYSAFE.

Second step : Proceed to type -- mvn install -DskipTests.

Third step : Open another terminal and decide which terminal will play the server and journalist role, getting inside on each terminal in the specific folder.

Fourth step : Initiate server, typing -- mvn compile exec:java , see if starts up.

Fifth step : Initiate researcher, typing -- ./target/appassembler/bin/researcher localhost 8080

Sixth step : Either you already did the sniffer guide and you have got a small data to work with within the server or you want register some observations to a specific sniffer and maybe not initialized yet. In order to go for the second option, type -- init <pathToFileData.txt> <snifferName> <address>.

Seventh step : Assuming that you have entered a non existent sniffer or a existent one giving it the right address that he already has, you will register the observations sucessfully.

Eigth step : Lets get some statistics about our data, type -- mean_dev to get the mean and standard deviation values for all non-infected citizens, in result line respectfully.

Nineth step : As you are a researcher, you have some extra permission and so you can get a statistic for a single target. Open a data.text inside /demo folder and look for a specific citizen ID, copy it out and type -- single_prob <ID("paste here)>. This will give you the probability of a specific citizen being infected, 1.00 if he already is infected or a value between 0 and 1,
according to our screet formula that uses the biggest time value of being with an infected citizen.

Tenth step : Imagine you wanted to test a different text file, just go ahead and type -- clear, this will clear all server information about observations, so you can get different statistics.

### Example for clientApp

Note: You might want to get the server clientApp started for some more operational controls, but for now it gives you the same as running the specific clients


First step : Make sure you are at the main folder of the project, G11-STAYSAFE.

Second step : Proceed to type -- mvn install -DskipTests.

Third step : Open another terminal and decide which terminal will play the server and journalist role, getting inside on each terminal in the specific folder.

Fourth step : Initiate server, typing -- mvn compile exec:java , see if starts up.

Fifth step : Initiate clientApp, typing -- mvn compile exec:java

Sixth step : You are just able to clear ping or init.
