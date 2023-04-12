# SYSC3303  
  
## Group Members  
Saffi Ali  
Atharva Kasture  
Henry Lin  
Ashwin Stoparczyk  

### Files Included  
BasicUML Iteration 1: A rough sketch of how the system was imagined  
Elevator_State_Machine: The state machine diagram of the Elevator  
Scheduler State Diagram: The state machine diagram of the Scheduler  
Doorclose_timing_diagram: Timing diagram for door closing  
System Sequence Diagram: Sequence diagram for the system  
UMLClassDiagram_Iteration4: UML class diagram of the system  
CommandData: A format for commands passed through the system  
Elevator: An elevator which can make and respond to commands  
ElevatorCommands: A collection of methods for use with lists of commands  
ElevatorSubsystem: Class that models the elevator's subsystems  
Floor: A floor which can make commands and for elevators to stop at  
Scheduler: Sorts and distributes commands throughout the system  
SchedulerQueue: Queue that processes scheduler requests  
TestElevator: Test cases for Elevator class  
TestElevatorSubsystem: Test cases for ElevatorSubsystem class  
TestFloor: Test cases for Floor class  
TestSheduler: Test cases for Scheduler class  
WorkQueue: Abstract class for a processing queue  
schedulerTestCommands.csv: CSV file which contains commands as input for Floor  

  
**To start the system, run Elevator, Floor, and Scheduler as separate programs. **  
  
### Group Responsibilities  
Saffi Ali: Split to RPC, Elevator position updates, Multithreaded scheduler, UML  
Atharva Kasture: Error Handling, Timing Diagrams  
Henry Lin: Elevator asking for jobs, Floor multithreading  
Ashwin Stoparczyk: Multithreaded scheduler, Scheduler algorithm, Sequence Diagrams  
_Test cases for each class written by their respective author._