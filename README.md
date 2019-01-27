# DataMiningProject

Developed during the Network Programming course.

Work Flow:

1.The client sends a csv log file.

2.The server reads each line and puts it in a working file, depending on the EventName;

  -Create an event from each line
  
  -Parse the description of the event to get the ids, which will be used from the algorithm
  
  -Map userId to userIp
  
  -Map EventContext to contextId
  
3.The server runs the FPGrowth algorithm to determine dependencies for each event;

4.The server decodes the output number of the algorithm and sends it in human readable format to the client.
