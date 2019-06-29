package cdenhart.maze;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;
import javalib.impworld.*;
import javalib.worldimages.*;

// represents the world state of a maze
public class MazeWorld extends World {
    Graph state;
    private ArrayList<ArrayList<Vertex>> initVertex;
    int worldHeight;
    int worldWidth;
    static final int CELL_SIZE = 30;
    private Player player;
    private boolean isPlaying;
    private boolean theGameIsOver;
    private String outcome;
    private boolean isAnimating;
    private String type;
    private boolean isSearching;
    private java.util.AbstractList<Vertex> worklist;
    private HashMap<Vertex, Vertex> cameFromEdge;
    private ArrayList<Vertex> alreadyChecked;
    private boolean startAnimating;
  
    public MazeWorld() {
      this.worldHeight = 20;
      this.worldWidth = 20;
      this.reset();
      this.startAnimating = false;
      this.player = new Player(this.state.allVertex, this.cameFromEdge);
    }
  
    // updates the world every tick
    public void onTick() {
      this.checkPlayer();
      this.worldEnds();
  
      if (this.isAnimating && this.startAnimating) {
        if (this.worklist.size() > 0 && this.isSearching) {
          this.nextStep();
        }
      }
    }
  
    // renders the scene
    @Override
    public WorldScene makeScene() {
      WorldScene canvas = new WorldScene(this.worldWidth * MazeWorld.CELL_SIZE + 10,
              this.worldHeight * MazeWorld.CELL_SIZE + 10);
  
      for (Vertex v : this.state.allVertex) {
        v.cellImage(canvas, this.isPlaying, new Posn(this.worldWidth - 1, this.worldHeight - 1));
      }
      return canvas;
    }
  
    // changes the world based on key input
    public void onKeyEvent(String ke) {
      switch (ke) {
        case "p":
          this.isPlaying = !this.isPlaying;
          break;
        case "left":
        case "right":
        case "up":
        case "down":
          if (this.isPlaying) {
            this.player.movePlayer(ke);
          }
          break;
        case "r":
          this.reset();
          break;
        case "a":
          if (this.isAnimating)
            this.isAnimating = false;
          else {
            this.initSearchConditions();
            this.isAnimating = true;
          }
          break;
        case "b":
          this.partialReset();
          this.startAnimating = true;
          this.type = "b";
          this.breadthSearch();
          break;
        case "d":
          this.partialReset();
          this.startAnimating = true;
          this.type = "d";
          this.depthSearch();
          break;
      }
    }
  
    // determines the next world scene based on the state of the world
    public WorldEnd worldEnds() {
      if (this.theGameIsOver) {
        return new WorldEnd(true, this.makeAFinalScene());
      }
      else {
        return new WorldEnd(false, this.makeScene());
      }
    }
  
    // shows the final world scene
    private WorldScene makeAFinalScene() {
      int width = this.worldWidth * CELL_SIZE;
      int height = this.worldHeight * CELL_SIZE;
      WorldScene scene = new WorldScene(width, height);
  
      scene.placeImageXY(new TextImage(this.outcome, 40, Color.BLACK), width / 2, height / 2);
  
      return scene;
    }
  
    // resets all world initialized conditions
    private void reset() {
      this.initVertex = new ArrayList<>();
      this.state = new Graph();
      this.isPlaying = false;
      this.updateWalls();
      this.theGameIsOver = false;
      this.isAnimating = false;
      this.isSearching = true;
      this.type = "b";
      this.startAnimating = false;
      this.cameFromEdge = new HashMap<>();
      this.alreadyChecked = new ArrayList<>();
      this.player = new Player(this.state.allVertex, this.cameFromEdge);
    }
  
    // resets everything, but keeps the same wall structure
    private void partialReset() {
      this.isPlaying = false;
      this.isSearching = true;
      this.type = "b";
      // store information about the path
      this.cameFromEdge = new HashMap<>();
      this.alreadyChecked = new ArrayList<>();
      for (Vertex v : this.state.allVertex) {
        v.hasPlayer = false;
        v.isVisited = false;
        v.isPath = false;
      }
    }
  
    // checks if the player is at the end
    private void checkPlayer() {
      if (this.player.getX() == (this.worldWidth - 1) && this.player.getY() == (this.worldHeight - 1)) {
        this.theGameIsOver = true;
        this.outcome = "You Win!";
      }
    }
  
    // uses nested for loops to add vertices to the graph and assign positions
    private void initVertices() {
      for (int i = 0; i < this.worldHeight; i++) {
        ArrayList<Vertex> colList = new ArrayList<>();
        for (int j = 0; j < this.worldWidth; j++) {
          Vertex nextVertex = new Vertex(j, i);
          colList.add(nextVertex);
        }
        this.initVertex.add(colList);
      }
    }
  
    // connects all of the verteces with edges
    private void initEdges() {
      this.initVertices();
      for (int i = 0; i < this.worldHeight; i++) {
        for (int j = 0; j < this.worldWidth; j++) {
          Vertex nextVertex = this.initVertex.get(i).get(j);
          this.addEdge(nextVertex, j, i, "t");
          this.addEdge(nextVertex, j, i, "r");
          this.addEdge(nextVertex, j, i, "b");
          this.addEdge(nextVertex, j, i, "l");
        }
      }
    }
  
    // connects the edge between two vertexes in the given direction
    private void addEdge(Vertex v, int x, int y, String dir) {
      int randomWeight = new Random().nextInt() * 10;
      Edge nextEdge = new Edge(randomWeight, dir);
      nextEdge.updateFrom(v);
      switch (dir) {
        case "t":
          if (this.isOutOfBounds(y - 1, this.worldHeight)) {
            nextEdge.updateTo(v);
          } else {
            nextEdge.updateTo(this.initVertex.get(y - 1).get(x));
          }
          break;
        case "r":
          if (this.isOutOfBounds(x + 1, this.worldWidth)) {
            nextEdge.updateTo(v);
          } else {
            nextEdge.updateTo(this.initVertex.get(y).get(x + 1));
          }
          break;
        case "b":
          if (this.isOutOfBounds(y + 1, this.worldHeight)) {
            nextEdge.updateTo(v);
          } else {
            nextEdge.updateTo(this.initVertex.get(y + 1).get(x));
          }
          break;
        default:
          if (this.isOutOfBounds(x - 1, this.worldWidth)) {
            nextEdge.updateTo(v);
          } else {
            nextEdge.updateTo(this.initVertex.get(y).get(x - 1));
          }
          break;
      }
    }
  
    // checks if the given index is out of bounds
    boolean isOutOfBounds(int i, int boundary) {
      return i >= boundary || i < 0;
    }
  
    // adds all of the initialized vertexes to this graph's list of vertex
    private void addVertices() {
      this.initEdges();
      for (int i = 0; i < this.worldHeight; i++) {
        for (int j = 0; j < this.worldWidth; j++) {
          Vertex nextVertex = this.initVertex.get(i).get(j);
          this.state.allVertex.add(nextVertex);
          this.state.initRepresentatives();
        }
      }
    }
  
    // removes walls between all cells connected by edges produced by kruskal's
    private void updateWalls() {
      this.addVertices();
      ArrayList<Edge> toRemove = this.state.kruskal();
      ArrayList<Edge> alreadyChecked = new ArrayList<>();
  
      for (Vertex v : this.state.allVertex) {
        for (Edge e : v.outEdges) {
          if (!(alreadyChecked.contains(e))) {
            if (toRemove.contains(e)) {
              Vertex adjacentCell = e.getTo();
  
              // check direction of connection
              if (e.getDirection().equals("t") && !(this.isOutOfBounds(v.y - 1, this.worldHeight))) {
                if (!(v.equals(adjacentCell))) {
                  v.topEdge = true;
                  adjacentCell.bottomEdge = true;
                }
              }
              if (e.getDirection().equals("r") && !(this.isOutOfBounds(v.x + 1, this.worldWidth))) {
                if (!(v.equals(adjacentCell))) {
                  v.rightEdge = true;
                  adjacentCell.leftEdge = true;
                }
              }
              if (e.getDirection().equals("b") && !(this.isOutOfBounds(v.y + 1, this.worldHeight))) {
                if (!(v.equals(adjacentCell))) {
                  v.bottomEdge = true;
                  adjacentCell.topEdge = true;
                }
              }
              if (e.getDirection().equals("l") && !(this.isOutOfBounds(v.x - 1, this.worldWidth))) {
                if (!(v.equals(adjacentCell))) {
                  v.leftEdge = true;
                  adjacentCell.rightEdge = true;
                }
              }
            }
          }
          alreadyChecked.add(e);
        }
      }
    }
  
    // solves the maze using breadth search
    private void breadthSearch() {
      this.initSearchConditions();
      if (!(this.isAnimating)) {
        this.implementSearch("b");
      }
    }
  
    // solves the maze using depth search
    private void depthSearch() {
      this.initSearchConditions();
      if (!(this.isAnimating)) {
        this.implementSearch("d");
      }
    }
  
    // implements the search regardless of type
    private void implementSearch(String type) {
  
      while (this.worklist.size() > 0 && this.isSearching) {
        this.nextStep();
      }
    }
  
    // implements the next step of the search
    @SuppressWarnings("unchecked")
    private void nextStep() {
  
      // get the next vertex
      Vertex next;
      if (this.type.equals("b")) {
        next = ((Queue<Vertex>) this.worklist).poll();
      }
      else if (this.type.equals("d")) {
        next = ((java.util.Stack<Vertex>) worklist).pop();
      }
      else {
        throw new RuntimeException("This search type does not exist");
      }
  
      // determine which action should be taken
      if (this.alreadyChecked.contains(next)) {
        return;
      }
      else if (next.equals(this.state.allVertex.get(this.state.allVertex.size() - 1))) {
        reconstruct(this.cameFromEdge, next);
        this.isSearching = false;
        return;
      }
      else {
        for (Edge e : next.outEdges) {
          if (((e.getDirection().equals("t") && next.topEdge)
                  || (e.getDirection().equals("r") && next.rightEdge)
                  || (e.getDirection().equals("l") && next.leftEdge)
                  || (e.getDirection().equals("b") && next.bottomEdge))
                  && this.cameFromEdge.get(e.getTo()) == null) {
            this.worklist.add(e.getTo());
            this.cameFromEdge.put(e.getTo(), next);
          }
        }
      }
      this.alreadyChecked.add(next);
      next.isVisited = true;
    }
  
    // travels back along the path, labeling each vertex as being part of it
    private void reconstruct(HashMap<Vertex, Vertex> hashMap, Vertex v) {
      v.isPath = true;
      if (!(v.x == 0 && v.y == 0)) {
        Vertex next = hashMap.get(v);
        this.reconstruct(hashMap, next);
      }
    }
  
    // initializes search conditions
    private void initSearchConditions() {
      if (this.type.equals("b")) {
        this.worklist = new java.util.LinkedList<>();
      }
      else if (this.type.equals("d")) {
        this.worklist = new java.util.Stack<>();
      }
      else {
        throw new RuntimeException("This search type does not exist");
      }
      this.worklist.add(this.state.allVertex.get(0));
    }
  }