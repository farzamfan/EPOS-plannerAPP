# plannerApp

This is the Android mobile application developed for the "Appliance-Level Flexible Scheduling for Socio-Technical Smart Grid Optimisation" paper.

#-------------------
How the application is used
#-------------------
This mobile application acts as users' scheduling agnet. 
Via this scheduling agent, users determine their household appliances, usage constraints, acceptable discomfort level, and their comfort vs. reliability preference. 
Next, users indicate their day-ahead appliance schedule with its usage duration and flexibility.
Using this information, the mobile application generates all the possible plans for a single appliance, and combines them with the plans from other appliances.
After this, the application enforces users' constraints (such as parallel usage of shower and oven), and preferences (such as local cost thresholds).
The plans are reported to the server (can be I-EPOS) for coordination and selection.

#-------------------
Steps on how to use the application
#-------------------
The attached "P-EPOS app user manual.pdf" describes the usage steps.
Note that the first time the application is used, the survey is shown.

#-------------------
Usage and implementation
#-------------------
Before using the application, it needs to be imported using Android Studio or any other capable IDE.
The app required gradle to be built propoerly.
Currently, the app is set up to forward the plans to an arbiterary email address. The setting can be found here:
	package com.example.scheduler.Mail.GMail.java
However, this approach is only feasible for less than 20 users, and not encrypted by default.
This needs to be changed before use.
However, it is advisable to use some publish/subscribe server to handle high number of users
There are some comments on the code to help with understanding it.

The main processing happanes using hte "schedulecreationlibrari".

#-------------------
Internal algorithms:
#-------------------

#-------------------
1- Creating combinations
#-------------------
This is regarding the generation of plans from the users schedules and combining them.


The combinations are made from treating the inputs as a numeral system converting that numeral system to decimal,
then when a combination is needed, retranslating it back into the original numeral system. 
Get combinations requires:
  the input number
  how many numbers were used to create that number
  The sizes of the ranges of values that each of those numbers could be

	For example:
	Take the input A, B, C, D which create the input X.
	A has a range of size i, B has a range of size j, C has a range of size k and D has a range of size l

		A = X%i
		B = ((X-A)/i)%j
		C = (((((X-A)/i)%j)-B)/j)%k
		D = (((((((X-A)/i)%j)-B)/j)-C)/k)%l

Through this method we can translate decimal into any numeral system of which we know the length and size of the ranges of each
column, even if each column has varying ranges.

#-------------------
2- How the scheduling library works
#-------------------

When an action is added, it also creates a list of all possible positions that it can be fixed in in its window. 
Each possible position also calculates and stores its own rating.

The threads then take the lists of all the possible positions for all the actions, and calculate the number of possible 
combinations.
This number is then divided by the number of cores. The parts of this number are ranges that are given to the threads
(where the number of threads = number of cores) who then step through the range 
(where the step = the number of combinations/the max number of combinations each thread can process). 
At each step, the number between 0 and the number of combinations represents a combination. 
This is passed into the method get combination, which returns a list of indexes for each list of action positions.

Taking the actions from this list at that position, the thread then checks if it is valid, 
if it is, it stores it, if not, it steps again until it exceeds its range.

Get combinations code: (i is the number of the combination on the initial call)

public static long[] getCombination(long i, Action[] lists, int index, long[] indeces){
  indeces[index] = (i-1)%lists[index].versions.length;
  long passOn = (((i-1)-indeces[index])/lists[index].versions.length)+1;
  
  if(index+1<lists.length){indeces = getCombination(passOn, lists, index+1, indeces);}
  return indeces;}

The list of lists of actions is then sorted using Collections.sort and a comparator. 
The comparator sums the ratings of the actions in each list, returning the higher of the two.
The flexibility then indicates the number of these to return.

#-------------------
3- How matching works:
#-------------------

The program to match the house and user via their demographic questions works in the following way:
1. The program looks for the answers to the following questions: 
  -House Occupancy
  -Year Built
  -House Type
  -Number of Bedrooms
2. The program examines the 20 houses from the REFIT data, and checks whether the answers to the questions match the details of any house.
3. The matching of answers is weighted. Some factors are more important than others.
    -The House Occupancy of a house, if equal to the user's answer, will add 0.533 to the score of that house.
    -The number of bedrooms in a house, if equal to the user's answer, will add 0.267 to the score of that house.
    -The type of house that a house is, if equal to the user's answer, will add 0.133 to the score of that house.
    -The Year the house was Built, if equal to the user's answer, will add 0.067 to the score of that house.
    (if a house has a score of 1, it is an exact match with all the answers of the user)
4. The house with the highest score is matched with the user. If multiple houses match, the choice is random between those houses.

#-------------------
Runtime and processing powert experiments
#-------------------
Here, we want to measure two things: power consumption and processing time.
Variables for this will be: # of inputs
	-Average duration of each input.
    -# of schedules required to be created.
    -Ranking scheme.
    -Sorting algorithms
For these experiments we want to repeat them between 50-100 times and measure the standard deviations between both Power
consumption and processing time.

We also want to measure procesing time and power consumption on the subtasks of the application. How long does the sorting 
algorithm take, the creation of schedules or the application of rank? And how much power is consumed during these subtasks?
The results are presented in the "Scheduler Execution Times 21-07-2017.pdf"
