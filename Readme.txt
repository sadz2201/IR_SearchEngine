ENVIRONMENT
===========
The yelp search engine program requires the following environment:
1. Lastest Java
   Download Link: http://www.oracle.com/technetwork/java/javase/downloads/index.html 
2. Eclipse IDE
   Download Link: https://eclipse.org/downloads/.
   
EXTERNAL LIBRARIES USED
====================
1. gson library
   Download Link: https://google-gson.googlecode.com/files/google-gson-2.2.4-release.zip
2. Lucene library
   Download Link: http://mirror.nus.edu.sg/apache/lucene/java/5.0.0/lucene-5.0.0.tgz

EXECUTION GUIDE
============

-  We can directly run the program from precompiled jar file `SeaEng.jar`.
In Linux operating systems, run the command:

		$ java -jar ./SeaEng.jar

- Then, set the path to your data directory using the command `:path path_to_data_dir`
E.g. if you put your data files in /Users/Siddharth/Documents/workspace/yelp_dataset_challenge_academic_dataset, type
		:path /Users/Siddharth/Documents/workspace/yelp_dataset_challenge_academic_dataset
  
- Now, start indexing by typing `:index`. (This can take a few minutes...)

- When everything is done, program creates a directory 'index-directory' inside the working directory, & stores the index here.

After this, we can search using one of the following two methods:
[1]Free-text search 

Just type the search query and hit ENTER
  + E.g    cafe
  
          rank |   score    |   docID    |         review        
        -------+------------+------------+----------------------- 
        #    1 |      2.576 |     391613 | Standard Madison Coffee Cafe  
        #    2 |      2.576 |     644333 | Cafe food perfect for seniors.  
        #    3 |      2.576 |     746479 |  Now a 24-hour cafe.  
        #    4 |      2.576 |     766454 | BEST CAFE AND AMBIANCE in the ENCORE!  
        #    5 |      2.576 |     946251 | Books, Magazines, and a cafe.  
        #    6 |      2.576 |    1082680 | Excellent crepes and cafe.  
        #    7 |      2.576 |    1409933 | The URTH Cafe of Scottsdale  
        #    8 |      2.254 |     165740 | Standard every day cafe food.  
        #    9 |      2.254 |     410773 | Great cafe with delicious muffins and...  
        #   10 |      2.254 |     527660 | Cafe vanilla frap with a banana in it...  

[2]Location-based search

Syntax:   :spatial <lon> <lat> <radius> <text>
For example:     :spatial -115 36 5 cafe 

This query returns documents containing the word `cafe`, whose location
(longtitude, latitude) is within the circle with center at (-115, 36) and radius= 5km. 

expected output:

          rank |   score    |   docID    |         review        
        -------+------------+------------+----------------------- 
        #    1 |      1.394 |     321597 | Stopped in for some of the biscuits a...  
        #    2 |      1.260 |    1440717 | Fiesta Henderson made the right decis...  
        #    3 |      1.127 |     322764 | Cute place.  I didn't expect it to be...  
        #    4 |      1.127 |    1113760 | This was my second visit for breakfas...  
        #    5 |      1.127 |    1305040 | An iced Cafe Mocha with an extra shot...  
        #    6 |      1.080 |     995803 | Went into Tropical Smoothie thinking ...  
        #    7 |      0.966 |     321438 | The late night menu at cafe fiesta is...  
        #    8 |      0.966 |    1297818 | Cheeseburger combo deal so good!! Sta...  
        #    9 |      0.966 |    1424732 | A vapor cafe for my liking, I have be...  
        #   10 |      0.966 |    1559568 | Been waiting for a long time for a pl...  

(P.S - Similarity computation & search relevance ranking is performed by Lucene's TopDoc package.
We haven't built our own functions for doing this)

To change the number of results to be shown :limit <n>
E.g.    :limit 5
Then the program will only return the top 5 search results.

Finally, use command ":help" for getting help information 
     and command ":quit" to exit :-)


COMPILING CODE
==============

To import our project into the eclipse, follow the following steps:
1. File -> Import
2. General -> Existing Projects into workspace
3. Choose "Select root directory" and then to the folder where the source code is stored (./SourceCode/SeaEng)

The following folders should be created in eclipse.
1. DataSet: The folder to store the dataset (downloaded from http://www.yelp.com/dataset_challenge/)
2. index-directory: Created by running the "index" command as described previously.

* Run "Console" class which greets you with the following message :

        WELCOME TO SEARCH-ENGINE PROGRAM

        Type `:help' for getting help.

        [SeaEng] >> 


* We can also run the preprocessing step to measure the index/dictionery size and indexing time when
using different Analyzers. First set the path to the dataset at line 107 in file Preprocessing.java. 
Then, run the "Preprocess" class to get the following result:

         |               Name |   Duration |   Dic_Size |   Idx_Size | 
         + ------------------ + ---------- + ---------- + ---------- + 
         |      Base_analyzer |     127.46 |   12168788 |     299035 | 
         |      Stem_analyzer |     126.86 |    7773117 |     194221 | 
         | LowerCase_analyzer |     108.64 |    7687388 |     189946 | 
         |  Stopword_analyzer |     105.08 |    9900608 |     242733 | 



