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

4.The server decodes the output numbers of the algorithm and sends it in human readable format to the client.

Notice this awesome approach:

    If we want to support another event:
      1.if the new event's description is similiar to the OneUserOneItem or TwoUserOneItem,
        we just need to add a new key-value pair in the SUPPORTED_EVENTS map e.g.
        <"Course module viewed", EventType.ONE_USER_ONE_ITEM>
      2.if the new event is different from the currently supported, 
        we just need to:
          -extend the abstract class Event and add the logic for that event.
          -add a new constant to the EventType enum.
          -add the <event,event.type> to the SUPPORTED_EVENTS.
        
