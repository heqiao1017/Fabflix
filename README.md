Note: All my fabflix codes are in project2, and all the log files, parsing script and HTML report, war file are shown on current folder.


## Task 1

### How did you use connection pooling?
I used connection pooling in all servlets, and I modified context.xml and web.xml


### File name, line numbers as in Github
* https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/project2/WebContent/META-INF/context.xml (line number in snapshot)
* https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/project2/WebContent/WEB-INF/web.xml  (line number in snapshot)

All the servlets in https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/tree/master/project2/src
* Checkout.java (line 77-98)
* DashboardActivity.java (line 86-107)
* ImportDataToDatabase.java (line 88-109)
* Login.java (line 89-110)
* MovieSuggestion.java (line 71-92)
* Search.java (line 89-110)
* ShoppingCart.java (line 78-99)
* SingleMovie.java (line 72-93)
* singleStar.java (line 61-82)

### Snapshots

#### context.xml
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/context.png)

#### web.xml
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/webXml.png)

#### all java servlets
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/pooling.png)

### How did you use Prepared Statements?
I used Prepared Statements in Search.java where link is https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/project2/src/Search.java


In context.xml I added ```cachePrepStmts=true```


### File name, line numbers as in Github
* Search.java (line 105-116 & 188-223)

### Snapshots
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/prepare1.png)


![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/prepare2.png)

## Task 2

### Address of AWS and Google instances
* instance 1: 13.56.76.169
* instance 2: 52.53.233.188
* instance 3: 13.56.13.201
* google instance: 35.230.29.106

### Have you verified that they are accessible? Does Fablix site get opened both on Google’s 80 port and AWS’ 8080 port?
Yes, they are all accessible

### How connection pooling works with two backend SQL and read/write requests were routed?
 * read/write requests were routed: I use the technique of https://dev.mysql.com/doc/connector-j/5.1/en/connector-j-master-slave-replication-connection.html where shows that performing read/write work on the master by setting the read-only flag to "false", and perform read on both master/slave by setting the read-only flag to "true" (where default is true); 
 * connection pooling works with two backend SQL: I only modified the servlet of need writing to the database and also in the context.xml I put both master and slave private ip in side the url.

### File name, line numbers as in Github
* https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/project2/WebContent/META-INF/context.xml (line number in snapshot)

https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/tree/master/project2/src
* Checkout.java
* DashboardActivity.java
* ImportDataToDatabase.java
### Snapshots

* context.xml
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/context.png)


* Checkout.java
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/checkout.png)


* DashboardActivity.java
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/dashboard_1.png)


![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/dashboard_2.png)


![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/dashboard_3.png)


![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/dashboard_4.png)


![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/dashboard_5.png)


![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/dashboard_6.png)



* ImportDataToDatabase.java
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/import1.png)


![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/import2.png)


## Task 3

### Have you uploaded the log file to Github? Where is it located?
Yes, they are on the current directory with README.md, UCI-Chenli-teaching/cs122b-winter18-team-19

### Have you uploaded the HTML file to Github? Where is it located?
Yes, they are on the current directory with README.md, UCI-Chenli-teaching/cs122b-winter18-team-19

### Have you uploaded the script  to Github? Where is it located?
Yes, they are on the current directory with README.md, UCI-Chenli-teaching/cs122b-winter18-team-19

### Have you uploaded the WAR file and README  to Github? Where is it located?
Yes, they are on the current directory with README.md, UCI-Chenli-teaching/cs122b-winter18-team-19


![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/logFiles.png)


## Note:
### The following is where in ```Search.java``` I modified to calculate the time for TS and TJ
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/time1.png)
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/time2.png)
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/time3.png)
![Alt text](https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/time4.png)

### I actually wrote a filter (https://github.com/UCI-Chenli-teaching/cs122b-winter18-team-19/blob/master/project2/src/TimingFilter.java) to timing the response time of the TS, but I did not have much time left to parse the TS from the tomcat log, so I kept using the TS I calculated inside the Search servlet

### If no connection pooling, need to uncomment the line 73-74 for ```Search.java```, and comment the line through 78-99
### If no prepare statement, need to uncomment the line 230 for ```Search.java```, and comment the line through 188-226
