DOC JONES SHORTEST PATH ALGORITHM - README

Run the ant build file (build.xml).

Algorithm:

Given a matrix of nodes, this finds the shortest path from the bottom right node to the top left node regardless of what nodes are connected. This will be done with the use of a single "plank" that connects two unconnected nodes if it results in a shorter path.

To use the algorithm:

1. Create a 2D array of Pillar objects, each having row and column values corresponding to their index in the array.

2. Create an UndirectedGraph instance (using jgrapht) and add all pillars as vertecies. (Can be done while creating array of Pillars)

3. Create Plank objects for each Pillar in the array, one joining a Pillar and it's northern, southern, western, and eastern neighbors if they exist. (Pillars on the edges of the array don't have all four)

4. Set the "usable" variable to true for the Planks that Indiana Jones (the algorithm) should be able to cross in the search for the shortest path.

5. Call the method "indy", passing it the UndirectedGraph as the layout of the temple Indiana Jones is navigating.

6. Save the result of the method call to a list and you have your shortest path, created with or without the use of the extra plank.


Credits:

Developer - Harry Nelken (hrn10@case.edu)

Created for EECS 293 - Vincenzo Liberatore - Software Craftsmanship