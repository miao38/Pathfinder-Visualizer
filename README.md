# Pathfinder-Visualizer
Built a pathfinder visualizer using Java. I've implemented A* search and an algorithm that I created called corner. 
Corner search works by using a priority queue. This algorithm doesn't necessarily find the shortest path. It always starts at the upper left corner of the first unexplored node. Then it makes its way around the unexplored nodes in a square formation.
Dijkstra's algorithm is one of the most well known pathfinding algorithms. It makes use of a priority queue and finds the shortest path by picking unvisited nodes near it with the shortest distance, and updates the neighbor's distance if that neightbors distance is currently larger than the new one. It continues this process until it reaches the end destination, and decides the shortest path based on these distances. A* is similar to Dijstra's algorithm except it takes the direction into account. Instead of just added the distance between neighbors, it also adds the distance from the end destination so we can reasonably ensure that we don't need to check nodes going in the wrong direction.

# Corner Search
![image](https://user-images.githubusercontent.com/54549208/75804801-9cef9280-5d4e-11ea-8077-7190740049bd.png)

# A* Search
![image](https://user-images.githubusercontent.com/54549208/74961882-d7ffd680-53dc-11ea-82c9-e15798d63a45.png)
