import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JComboBox;

public class Pathfinder {
    class Node {
        //Start = 0, End = 1, Wall = 2, Empty = 3, Visited = 4, Path = 5
        private int cellType = 0;
        private int hops, x, y, lastX, lastY;
        private double distToEnd = 0;

        public Node(int type, int x, int y) {
            this.cellType = type;
            this.x = x;
            this.y = y;
            this.hops = -1;
        }

        //finds the euclidian distance to the end node
        public double getEuclidDist() {
            int xdif = Math.abs(x - finishX);
            int ydif = Math.abs(y - finishY);
            distToEnd = Math.sqrt((xdif * xdif) + (ydif * ydif));
            return distToEnd;
        }

        //getter methods
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getLastX() {
            return lastX;
        }

        public int getLastY() {
            return lastY;
        }

        public int getType() {
            return cellType;
        }

        public int getHops() {
            return hops;
        }

        //setter methods
        public void setType(int type) {
            cellType = type;
        }

        public void setLastNode(int x, int y) {
            lastX = x;
            lastY = y;
        }

        public void setHops(int hops) {
            this.hops = hops;
        }
    }

    //Class for pathfinder algorithms
    class Algorithm {
        /**
         * Dijkstra workds by going to the closest node and updating that distance
         * if it is less than infinity at first. Then it'll go to the next node with
         * the lowest value and keep doing this until it reaches the goal and finds the
         * shortest path
         * <p>
         * Using a priority queue to keep track of the nodes that it needs to explore
         * when each node is explored, all of its neighbors are added to the queue
         * then once a node is explore it is deleted from the queue
         * <p>
         * Using an ArrayList to represent the priority queue
         * <p>
         * A seperate ArrayList is returned from the method that explores a nodes neighbors
         * this ArrayList will contain all the nodes that were explores, then it is added to the queue
         * <p>
         * The variable hops in each node represents the number of nodes traveled from the start
         */
        public void Dijkstra() {
            ArrayList<Node> priority = new ArrayList<Node>(); //priority queue
            priority.add(map[startX][startY]); //adding the start to the queue
            while (solving) {
                if (priority.size() <= 0) { //if the queue is 0, then no path was found
                    solving = false;
                    JOptionPane.showMessageDialog(frame, "No path was found");
                    break;
                }
                int hops = priority.get(0).getHops() + 1; //incrementing the hops variable
                ArrayList<Node> visited = exploreNeighbors(priority.get(0), hops); //creating an ArrayList of nodes that were explores
                if (visited.size() > 0) {
                    priority.remove(0); //removing the node from the queue
                    priority.addAll(visited); //adding all the new nodes to the queue
                    update();
                    delay();
                } else //if no nodes were explored then just remove the node fro the queue
                    priority.remove(0);
            }
        }

        /**
         * A* words basically the same as Dijkstra by creating a priority queue and moving outwards
         * until it finds the end
         * However, it takes direction into account unlike Dijkstra
         * Basically, this means that A* ignores nodes that aren't going in the right direction
         * so the priority queue will check nodes closer to the end first
         * <p>
         * So we will sort the queue according to hops plus the distance until the end
         */
        public void AStar() {
            ArrayList<Node> priority = new ArrayList<Node>();
            priority.add(map[startX][startY]);
            while (solving) {
                if (priority.size() <= 0) {
                    solving = false;
                    break;
                }
                int hops = priority.get(0).getHops() + 1;
                ArrayList<Node> visited = exploreNeighbors(priority.get(0), hops);
                if (visited.size() > 0) {
                    priority.remove(0);
                    priority.addAll(visited);
                    update();
                    delay();
                } else
                    priority.remove(0);
                sortQueue(priority); //sorting the priority queue
            }
        }

        /**
         * Sorting algorithm to sort the nodes for priority queue
         */
        public ArrayList<Node> sortQueue(ArrayList<Node> sort) {
            int first = 0;
            while (first < sort.size()) {
                int second = first;
                for (int i = first + 1; i < sort.size(); i++) {
                    if (sort.get(i).getEuclidDist() + sort.get(i).getHops() < sort.get(second).getEuclidDist() + sort.get(second).getHops())
                        second = i;
                }
                if (first != second) {
                    Node temp = sort.get(first);
                    sort.set(first, sort.get(second));
                    sort.set(second, temp);
                }
                first++;
            }
            return sort;
        }

        /**
         * exploring the neightbors
         */
        public ArrayList<Node> exploreNeighbors(Node current, int hops) {
            ArrayList<Node> explored = new ArrayList<Node>(); //list of nodes that have been explored
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int xBound = current.getX() + i;
                    int yBound = current.getY() + j;
                    if ((xBound > -1 && xBound < cells) && (yBound > -1 && yBound < cells)) {
                        Node neighbor = map[xBound][yBound];
                        //checks if the node isn't a wall and that it hasn't been explored yet
                        if ((neighbor.getHops() == -1 || neighbor.getHops() > hops) && neighbor.getType() != 2) {
                            visited(neighbor, current.getX(), current.getY(), hops);
                            explored.add(neighbor); //adds the node to the list
                        }
                    }
                }
            }
            return explored;
        }

        /**
         * Checking if the node has been visited
         */
        public void visited(Node current, int lastX, int lastY, int hops) {
            if (current.getType() != 0 && current.getType() != 1) //checking if the node is not the start or end node
                current.setType(4); //setting node to visited
            current.setLastNode(lastX, lastY); //keeping track of the node that this node came from, i.e. the path
            current.setHops(hops); //setting the hops from the start
            checks++;
            if (current.getType() == 1) //if the node is at finish, then backtracking to get the path
                path(current.getLastX(), current.getLastY(), hops);
        }

        /**
         * Getting the path from start to finish
         */
        public void path(int prevX, int prevY, int hops) {
            length = hops;
            while (hops > 1) {
                Node current = map[prevX][prevY];
                current.setType(5);
                prevX = current.getLastX();
                prevY = current.getLastY();
                hops--;
            }
            solving = false;
        }
    }

    //Grid
    JFrame frame;
    //General Vairables
    private int cells = 20;
    private int delay = 30;
    private double dense = .5;
    private double density = (cells * cells) * .5;
    private int startX = -1;
    private int startY = -1;
    private int finishX = -1;
    private int finishY = -1;
    private int tool = 0;
    private int checks = 0;
    private int length = 0;
    private int currAlg = 0;
    private int WIDTH = 850;
    private final int HEIGHT = 650;
    private final int MSIZE = 600;
    private int CSIZE = MSIZE / cells;
    //Arrays
    private String[] algroithms = {"Dijkstra", "A*"};
    private String[] tools = {"Start", "End", "Wall", "Eraser"};
    //Booleans
    private boolean solving = false;
    //Util
    Node[][] map;
    Algorithm alg = new Algorithm();
    Random rand = new Random();
    //Sliders
    JSlider size = new JSlider(1, 5, 2);
    JSlider speed = new JSlider(0, 500, delay);
    JSlider obstacles = new JSlider(1, 100, 50);
    //Labels
    JLabel algLable = new JLabel("Algorithms");
    JLabel toolboxLabel = new JLabel("Toolbox");
    JLabel sizeLabel = new JLabel("Size:");
    JLabel cellsLabel = new JLabel(cells + "x" + cells);
    JLabel delayLabel = new JLabel("Delay:");
    JLabel msLabel = new JLabel(delay + "ms");
    JLabel denLabel = new JLabel("Density:");
    JLabel densityLabel = new JLabel(obstacles.getValue() + "%");
    JLabel checksLabel = new JLabel("Checks: " + checks);
    JLabel lengthLabel = new JLabel("Path Length: " + length);
    //Buttons
    JButton searchButton = new JButton("Start Algorithm");
    JButton resetButton = new JButton("Reset");
    JButton genMapButton = new JButton("Generate Maze");
    JButton clearMapButton = new JButton("Clear Map");
    JButton instructionsButton = new JButton("Instructions");
    //Drop Down Menu
    JComboBox algorithmsDDM = new JComboBox(algroithms);
    JComboBox toolDDM = new JComboBox(tools);
    //Panels
    JPanel toolPanel = new JPanel();
    //Grid
    Map canvas;
    //Border
    Border lowerEtChed = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

    /**Main Method*/
    public static void main(String[] args) {
        new Pathfinder();
    }
    /**Constructor*/
    public Pathfinder() {
        clearMap();
        initialize();
    }

    /**Generating the map*/
    public void generateMap() {
        clearMap(); //creating a blank map to start with
        for (int i = 0; i < density; i++) {
            Node current;
            do {
                int x = rand.nextInt(cells);
                int y = rand.nextInt(cells);
                current = map[x][y]; //finding a random node in the grid
            } while (current.getType() == 2); //if it is already a wall, finding a new one
            current.setType(2); //setting the node to be a wall
        }
    }

    /**Clearing the map*/
    public void clearMap() {
        finishX = -1; //resetting the start and finish
        finishY = -1;
        startX = -1;
        startY = -1;
        map = new Node[cells][cells]; //creating a new map of nodes
        for (int x = 0; x < cells; x++) {
            for (int y = 0; y < cells; y++) {
                map[x][y] = new Node(3, x, y); //setting all the nodes to empty
            }
        }
        reset(); //resetting some variables
    }

    /**Resetting the map*/
    public void resetMap() {
        for (int x = 0; x < cells; x++) {
            for (int y = 0; y < cells; y++) {
                Node current = map[x][y];
                //checking to see if the current node was visited or the final path
                if (current.getType() == 4 || current.getType() == 5)
                    map[x][y] = new Node(3, x, y); //resetting the node to an empty node
            }
        }
        //resetting the start and end node
        if (startX > -1 && startY > -1) {
            map[startX][startY] = new Node(0, startX, startY);
            map[startX][startY].setHops(0);
        }
        if (finishX > -1 && finishY > -1)
            map[finishX][finishY] = new Node(1, finishX, finishY);
        reset(); //resetting some variables
    }

    /**Initializing the GUI elements*/
    private void initialize() {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle("Path Finder");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        toolPanel.setBorder(BorderFactory.createTitledBorder(lowerEtChed, "Controls"));
        int space = 25;
        int buffer = 45;

        toolPanel.setLayout(null);
        toolPanel.setBounds(10, 10, 210, 600);

        searchButton.setBounds(40, space, 120, 25);
        toolPanel.add(searchButton);
        space += buffer;

        resetButton.setBounds(40, space, 120, 25);
        toolPanel.add(resetButton);
        space += buffer;

        genMapButton.setBounds(40, space, 120, 25);
        toolPanel.add(genMapButton);
        space += 40;

        clearMapButton.setBounds(40, space, 120, 25);
        toolPanel.add(clearMapButton);
        space += 40;

        algLable.setBounds(40, space, 120, 25);
        toolPanel.add(algLable);
        space += 25;

        algorithmsDDM.setBounds(40, space, 120, 25);
        toolPanel.add(algorithmsDDM);
        space += 40;

        toolboxLabel.setBounds(40, space, 120, 25);
        toolPanel.add(toolboxLabel);
        space += 25;

        toolDDM.setBounds(40, space, 120, 25);
        toolPanel.add(toolDDM);
        space += buffer;

        sizeLabel.setBounds(15, space, 40, 25);
        toolPanel.add(sizeLabel);
        size.setMajorTickSpacing(10);
        size.setBounds(60, space, 100, 25);
        toolPanel.add(size);
        cellsLabel.setBounds(160, space, 40, 25);
        toolPanel.add(cellsLabel);
        space += buffer;

        delayLabel.setBounds(15, space, 50, 25);
        toolPanel.add(delayLabel);
        speed.setMajorTickSpacing(5);
        speed.setBounds(60, space, 100, 25);
        toolPanel.add(speed);
        msLabel.setBounds(160, space, 40, 25);
        toolPanel.add(msLabel);
        space += buffer;

        denLabel.setBounds(15, space, 100, 25);
        toolPanel.add(denLabel);
        obstacles.setMajorTickSpacing(5);
        obstacles.setBounds(60, space, 100, 25);
        toolPanel.add(obstacles);
        densityLabel.setBounds(160, space, 100, 25);
        toolPanel.add(densityLabel);
        space += buffer;

        checksLabel.setBounds(15, space, 100, 25);
        toolPanel.add(checksLabel);
        space += buffer;

        lengthLabel.setBounds(15, space, 100, 25);
        toolPanel.add(lengthLabel);
        space += buffer;

        instructionsButton.setBounds(40, space, 120, 25);
        toolPanel.add(instructionsButton);

        frame.getContentPane().add(toolPanel);

        canvas = new Map();
        canvas.setBounds(230, 10, MSIZE + 1, MSIZE + 1);
        frame.getContentPane().add(canvas);

        /**Action Listeners*/
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                if ((startX > -1 && startY > -1) && (finishX > -1 && finishY > -1))
                    solving = true;
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMap();
                update();
            }
        });
        genMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMap();
                update();
            }
        });
        clearMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMap();
                update();
            }
        });
        algorithmsDDM.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                currAlg = algorithmsDDM.getSelectedIndex();
                update();
            }
        });
        toolDDM.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                tool = toolDDM.getSelectedIndex();
            }
        });
        size.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                cells = size.getValue() * 10;
                clearMap();
                resetMap();
                update();
            }
        });
        speed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                delay = speed.getValue();
                update();
            }
        });
        obstacles.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                dense = (double) obstacles.getValue() / 100;
                update();
            }
        });
        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Welcome to Pathfinder Visualizer\n" +
                        "First select your path finding algorithm from the Algorithms drop down box.\n" +
                        "Next go to the tool box and select start and click a square on the grid to place the starting node\n" +
                        "Repeat steps for the end node and walls\n" +
                        "When you're ready, click on the start search button and watch the magic happen\n" +
                        "When the search is finished, it will indicate the shortest path with yellow boxes\n" +
                        "Click on the reset button and the repeat the steps\n" +
                        "The density button determines how many walls will be generated when you click the generate maze button");
            }
        });
        //starting the search
        startSearch();
    }

    /**
     * starting to search for the end node
     */
    public void startSearch() {
        if (solving) {
            switch (currAlg) {
                case 0:
                    alg.Dijkstra();
                    break;
                case 1:
                    alg.AStar();
                    break;
            }
        }
        pause();
    }

    /**
     * pausing
     */
    public void pause() {
        int i = 0;
        while (!solving) {
            i++;
            if (i > 500)
                i = 0;
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
        }
        startSearch();
    }

    /**
     * Updating the GUI
     */
    public void update() {
        density = (cells * cells) * dense;
        CSIZE = MSIZE / cells;
        canvas.repaint();
        cellsLabel.setText(cells + "x" + cells);
        msLabel.setText(delay + "ms");
        lengthLabel.setText("Path Length: " + length);
        densityLabel.setText(obstacles.getValue() + "%");
        checksLabel.setText("Checks: " + checks);
    }

    /**
     * resetting the method
     */
    public void reset() {
        solving = false;
        length = 0;
        checks = 0;
    }

    /**
     * delaying method
     */
    public void delay() {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
        }
    }

    /**
     * Map class
     */
    class Map extends JPanel implements MouseListener, MouseMotionListener {
        public Map() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        /**
         * coloring the nodes
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int x = 0; x < cells; x++) { //coloring each node
                for (int y = 0; y < cells; y++) {
                    switch (map[x][y].getType()) {
                        case 0:
                            g.setColor(Color.GREEN);
                            break;
                        case 1:
                            g.setColor(Color.RED);
                            break;
                        case 2:
                            g.setColor(Color.BLACK);
                            break;
                        case 3:
                            g.setColor(Color.WHITE);
                            break;
                        case 4:
                            g.setColor(Color.CYAN);
                            break;
                        case 5:
                            g.setColor(Color.YELLOW);
                            break;
                    }
                    g.fillRect(x * CSIZE, y * CSIZE, CSIZE, CSIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x * CSIZE, y * CSIZE, CSIZE, CSIZE);
                }
            }
        }


        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            resetMap(); //resets the grid whenever clicked
            try {
                int x = e.getX() / CSIZE; //getting the x and y of where the mouse clicked
                int y = e.getY() / CSIZE;
                Node current = map[x][y];
                switch (tool) {
                    case 0: { //start node
                        if (current.getType() != 2) { //if not a wall
                            if (startX > -1 && startY > -1) { //if start exists, setting it to empty
                                map[startX][startY].setType(3);
                                map[startX][startY].setHops(-1);
                            }
                            current.setHops(0);
                            startX = x; //setting the start to x and y
                            startY = y;
                            current.setType(0); //setting the clicked node to be start
                        }
                        break;
                    }
                    case 1: { //end node
                        if (current.getType() != 2) {
                            if (finishX > -1 && finishY > -1) //if an end node exist, setting it to empty
                                map[finishX][finishX].setType(3);
                            finishX = x;
                            finishY = y;
                            current.setType(1); //setting the clicked node to be the end
                        }
                        break;
                    }
                    default:
                        if (current.getType() != 0 && current.getType() != 1)
                            current.setType(tool);
                        break;
                }
                Pathfinder.this.update();
            } catch (Exception ex) {}
        }

        @Override
        public void mouseReleased(MouseEvent e){

        }

        @Override
        public void mouseEntered (MouseEvent e){

        }

        @Override
        public void mouseExited (MouseEvent e){

        }

        @Override
        public void mouseDragged (MouseEvent e){
            try {
                int x = e.getX() / CSIZE;
                int y = e.getY() / CSIZE;
                Node current = map[x][y];
                if ((tool == 2 || tool == 3) && (current.getType() != 0 && current.getType() != 1))
                    current.setType(tool);
                Pathfinder.this.update();
            } catch (Exception ex) {
            }
        }

        @Override
        public void mouseMoved (MouseEvent e){

        }
    }
}