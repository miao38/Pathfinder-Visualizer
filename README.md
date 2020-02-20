# Pathfinder-Visualizer
Built a pathfinder visualizer using Java. I've implemented Dijkstra's algorithm and A* search
Dijkstra's algorithm is one of the most well known pathfinding algorithms. It makes use of a priority queue and finds the shortest path by picking unvisited nodes near it with the shortest distance, and updates the neighbor's distance if that neightbors distance is currently larger than the new one. It continues this process until it reaches the end destination, and decides the shortest path based on these distances.
A* is similar to Dijstra's algorithm except it takes the direction into account. Instead of just added the distance between neighbors, it also adds the distance from the end destination so we can reasonably ensure that we don't need to check nodes going in the wrong direction.
![image](https://user-images.githubusercontent.com/54549208/74961728-89ead300-53dc-11ea-949e-d893cf71f4ff.png)
