
// Assignment 10 - Part 2
// lau brenda
// brendalau
// denhart charles
// cccdenhart

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;
import java.util.Comparator;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

/* DOCUMENTATION
 * "p" -- turns player mode on/off
 * "up/down/left/right" -- moves player if player mode is on
 * "r" -- resets the board
 * "a" -- turns animation mode on/off 
 *        (if on it will build search on tick, else will render instantaneously)
 * "b" -- displays a breadth search
 * "d" -- displays a depth search
 * NOTE --> to change to 100 x 60, change in MazeWorld:
 *      - CELL_SIZE to 10
 *      - this.worldHeight to 60
 *      - this.worldWidth to 100
 */

// Compares two generic items
interface IComparator<T> {

  // Returns a negative number if t1 comes before t2 in this order
  // Returns zero if t1 is tied with r2 in this order
  // Returns a positive number if t1 comes after t2 in this order
  int compare(T t1, T t2);
}

class EdgeComparator implements Comparator<Edge> {

  // Returns a negative number if r1 comes before r2 in this order
  // Returns zero if r1 is tied with r2 in this order
  // Returns a positive number if r1 comes after r2 in this order
  public int compare(Edge t1, Edge t2) {
    if (t1.weight < t2.weight) {
      return -1;
    }
    else if (t1.weight == t2.weight) {
      return 0;
    }
    else {
      return 1;
    }
  }
}

// represents a series of vertices
class Graph {
  ArrayList<Vertex> allVertex;
  HashMap<Vertex, Vertex> representatives;

  Graph() {
    this.allVertex = new ArrayList<Vertex>();
    this.representatives = new HashMap<Vertex, Vertex>();

  }

  // EFFECT: intializes representatives
  void initRepresentatives() {
    for (Vertex v : this.allVertex) {
      this.representatives.put(v, v);
    }
  }

  // returns a sorted list of all edges in this graph
  ArrayList<Edge> makeEdges() {

    ArrayList<Edge> edgeList = new ArrayList<Edge>();

    for (Vertex v : this.allVertex) {
      for (Edge e : v.outEdges) {
        if (!edgeList.contains(e)) {
          edgeList.add(e);
        }
      }
    }
    Collections.sort(edgeList, new EdgeComparator());
    return edgeList;
  }

  // initialize nodes to themselves
  ArrayList<Edge> kruskal() {
    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    ArrayList<Edge> worklist;

    this.initRepresentatives();

    worklist = this.makeEdges();

    // adds edges until there are v - 1 edges
    while ((edgesInTree.size() < (this.allVertex.size() - 1)) && worklist.size() > 0) {
      Edge nextEdge = worklist.remove(0);
      Vertex from = nextEdge.from;
      Vertex to = nextEdge.to;

      if (!(find(from).equals(find(to)))) {
        edgesInTree.add(nextEdge);
        this.union(to, from);
      }
    }
    return edgesInTree;
  }

  // find the representative of this Vertex
  Vertex find(Vertex v) {
    if (this.representatives.get(v).equals(v)) {
      return v;
    }
    else {
      return find(this.representatives.get(v));
    }
  }

  // EFFECT: unions the representatives of two vertices
  void union(Vertex v1, Vertex v2) {
    this.representatives.put(this.find(v1), this.find(v2));
  }
}

// represents a single vertex
class Vertex {
  int x;
  int y;
  ArrayList<Edge> outEdges; // edges from this node

  boolean leftEdge;
  boolean topEdge;
  boolean rightEdge;
  boolean bottomEdge;

  boolean isVisited;
  boolean isPath;
  boolean hasPlayer;

  Vertex(int x, int y) {
    this.outEdges = new ArrayList<Edge>();
    this.x = x;
    this.y = y;

    this.leftEdge = false;
    this.topEdge = false;
    this.rightEdge = false;
    this.bottomEdge = false;

    this.isVisited = false;
    this.isPath = false;
    this.hasPlayer = false;
  }

  // create hash code
  public int hashCode() {
    return this.x * 10000 + this.y;
  }

  // Override equals
  public boolean equals(Object other) {
    if (!(other instanceof Vertex)) {
      return false;
    }
    Vertex that = (Vertex) other;
    return this.x == that.x && this.y == that.y;
  }

  void cellImage(WorldScene acc, boolean isPlaying, Posn lastCell) {
    // initial variables
    int size = MazeWorld.CELL_SIZE;
    int imageX = this.x * size + size / 2 + 5;
    int imageY = this.y * size + size / 2 + 5;

    // create cell image
    WorldImage cell = new RectangleImage(size, size, OutlineMode.SOLID, Color.LIGHT_GRAY);
    if (this.isVisited) {
      cell = new RectangleImage(size, size, OutlineMode.SOLID, Color.cyan);
    }
    if (this.isPath) {
      cell = new RectangleImage(size, size, OutlineMode.SOLID, Color.blue);
    }
    if (this.x == lastCell.x && this.y == lastCell.y && isPlaying) {
      cell = new RectangleImage(size, size, OutlineMode.SOLID, Color.MAGENTA);
    }
    if (this.hasPlayer && isPlaying) {
      cell = new RectangleImage(size, size, OutlineMode.SOLID, Color.red);
    }
    acc.placeImageXY(cell, imageX, imageY);

    // add edges
    WorldImage horEdge = new LineImage(new Posn(size - 1, 0), Color.BLACK);
    WorldImage verEdge = new LineImage(new Posn(0, size), Color.BLACK);
    if (!(this.leftEdge)) {
      acc.placeImageXY(verEdge, imageX - size / 2, imageY);
    }
    if (!(this.topEdge)) {
      acc.placeImageXY(horEdge, imageX, imageY - size / 2);
    }
    if (!(this.rightEdge)) {
      acc.placeImageXY(verEdge, imageX + size / 2, imageY);
    }
    if (!(this.bottomEdge)) {
      acc.placeImageXY(horEdge, imageX, imageY + size / 2);
    }
  }
}

// represents a connection between vertices
class Edge {
  Vertex from;
  Vertex to;
  int weight;
  String direction;

  Edge(int weight, String direction) {
    this.weight = weight;
    this.direction = direction;
  }

  // override equals
  public boolean equals(Object other) {
    if (!(other instanceof Edge)) {
      return false;
    }
    Edge that = (Edge) other;
    return this.from.equals(that.from) && this.to.equals(that.to) && this.weight == that.weight
            && this.direction.equals(that.direction);
  }

  // override hashCode
  public int hashCode() {
    return this.from.hashCode() * 10 + this.to.hashCode() + this.weight;
  }

  // EFFECT: updates the To vertex to the given
  void updateTo(Vertex given) {
    this.to = given;
  }

  // EFFECT: updates the From vertex to the given and then updates the given out
  // edges
  void updateFrom(Vertex given) {
    this.from = given;
    given.outEdges.add(this);
  }

}

// represents the player
class Player {
  int x;
  int y;
  ArrayList<Vertex> vertices;
  Vertex curVert = new Vertex(0, 0);
  HashMap<Vertex, Vertex> cameFromEdge;

  Player(ArrayList<Vertex> vertices, HashMap<Vertex, Vertex> cameFromEdge) {
    this.x = 0;
    this.y = 0;
    this.vertices = vertices;
    this.updateVertex();
    this.cameFromEdge = cameFromEdge;
  }

  // moves the player on arrow key input
  void movePlayer(String ke) {
    if (ke.equals("left")) {
      if (this.curVert.leftEdge) {
        this.x--;
      }
    }
    else if (ke.equals("right")) {
      if (this.curVert.rightEdge) {
        this.x++;
      }
    }
    else if (ke.equals("up")) {
      if (this.curVert.topEdge) {
        this.y--;
      }
    }
    else if (ke.equals("down")) {
      if (this.curVert.bottomEdge) {
        this.y++;
      }
    }
    this.updateVertex();
  }

  // returns the vertex that the player is on
  void updateVertex() {
    for (Vertex v : this.vertices) {
      v.hasPlayer = false;
      if (v.equals(new Vertex(this.x, this.y))) {
        this.curVert = v;
        v.hasPlayer = true;
        v.isVisited = true;
      }
    }
  }

  // maps the vertex the player has been on to the world's hashmap
  void updateMap() {
    for (Edge e : this.curVert.outEdges) {
      if (((e.direction.equals("t") && this.curVert.topEdge)
              || (e.direction.equals("r") && this.curVert.rightEdge)
              || (e.direction.equals("l") && this.curVert.leftEdge)
              || (e.direction.equals("b") && this.curVert.bottomEdge))
              && this.cameFromEdge.get(e.to) == null) {
        this.cameFromEdge.put(e.to, this.curVert);
      }
    }
  }
}

// represents the world state of a maze
class MazeWorld extends World {
  Graph state;
  ArrayList<ArrayList<Vertex>> initVertex;
  int worldHeight;
  int worldWidth;
  static final int CELL_SIZE = 30;
  Player player;
  boolean isPlaying;
  boolean theGameIsOver;
  String outcome;
  boolean isAnimating;
  String type;
  boolean isSearching;
  java.util.AbstractList<Vertex> worklist;
  HashMap<Vertex, Vertex> cameFromEdge;
  ArrayList<Vertex> alreadyChecked;
  boolean startAnimating;

  MazeWorld() {
    this.worldHeight = 20;
    this.worldWidth = 20;
    this.initVertex = new ArrayList<ArrayList<Vertex>>();
    this.state = new Graph();
    this.isPlaying = false;
    this.updateWalls();
    this.theGameIsOver = false;
    this.isAnimating = false;
    this.isSearching = true;
    this.type = "b";
    // store information about the path
    this.cameFromEdge = new HashMap<Vertex, Vertex>();
    this.alreadyChecked = new ArrayList<Vertex>();
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
    if (ke.equals("p")) {
      if (this.isPlaying) {
        this.isPlaying = false;
      }
      else {
        this.isPlaying = true;
      }
    }
    else if (ke.equals("left") || ke.equals("right") || ke.equals("up") || ke.equals("down")) {
      if (this.isPlaying) {
        this.player.movePlayer(ke);
      }
    }
    else if (ke.equals("r")) {
      this.reset();
    }
    else if (ke.equals("a")) {
      if (this.isAnimating)
        this.isAnimating = false;
      else {
        this.initSearchConditions();
        this.isAnimating = true;
      }
    }
    else if (ke.equals("b")) {
      this.partialReset();
      this.startAnimating = true;
      this.type = "b";
      this.breadthSearch();
    }
    else if (ke.equals("d")) {
      this.partialReset();
      this.startAnimating = true;
      this.type = "d";
      this.depthSearch();
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
  WorldScene makeAFinalScene() {
    int width = this.worldWidth * CELL_SIZE;
    int height = this.worldHeight * CELL_SIZE;
    WorldScene scene = new WorldScene(width, height);

    scene.placeImageXY(new TextImage(this.outcome, 40, Color.BLACK), width / 2, height / 2);

    return scene;
    // this.reconstruct(this.cameFromEdge,
    // this.state.allVertex.get(this.state.allVertex.size() - 1));
    // return this.makeScene();

  }

  // resets all world initialized conditions
  void reset() {
    this.initVertex = new ArrayList<ArrayList<Vertex>>();
    this.state = new Graph();
    this.isPlaying = false;
    this.updateWalls();
    this.theGameIsOver = false;
    this.isAnimating = false;
    this.isSearching = true;
    this.type = "b";
    this.startAnimating = false;
    this.cameFromEdge = new HashMap<Vertex, Vertex>();
    this.alreadyChecked = new ArrayList<Vertex>();
    this.player = new Player(this.state.allVertex, this.cameFromEdge);
  }

  // resets everything, but keeps the same wall structure
  void partialReset() {
    this.isPlaying = false;
    this.isSearching = true;
    this.type = "b";
    // store information about the path
    this.cameFromEdge = new HashMap<Vertex, Vertex>();
    this.alreadyChecked = new ArrayList<Vertex>();
    for (Vertex v : this.state.allVertex) {
      v.hasPlayer = false;
      v.isVisited = false;
      v.isPath = false;
    }
  }

  // checks if the player is at the end
  void checkPlayer() {
    if (this.player.x == (this.worldWidth - 1) && this.player.y == (this.worldHeight - 1)) {
      this.theGameIsOver = true;
      this.outcome = "You Win!";
    }
  }

  // uses nested for loops to add vertices to the graph and assign positions
  void initVertices() {
    for (int i = 0; i < this.worldHeight; i++) {
      ArrayList<Vertex> colList = new ArrayList<Vertex>();
      for (int j = 0; j < this.worldWidth; j++) {
        Vertex nextVertex = new Vertex(j, i);
        colList.add(nextVertex);
      }
      this.initVertex.add(colList);
    }
  }

  // connects all of the verteces with edges
  void initEdges() {
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

  // connects the edge between two verteces in the given direction
  void addEdge(Vertex v, int x, int y, String dir) {
    int randomWeight = new Random().nextInt() * 10;
    Edge nextEdge = new Edge(randomWeight, dir);
    nextEdge.updateFrom(v);
    if (dir.equals("t")) {
      if (this.isOutOfBounds(y - 1, this.worldHeight)) {
        nextEdge.updateTo(v);
      }
      else {
        nextEdge.updateTo(this.initVertex.get(y - 1).get(x));
      }
    }
    else if (dir.equals("r")) {
      if (this.isOutOfBounds(x + 1, this.worldWidth)) {
        nextEdge.updateTo(v);
      }
      else {
        nextEdge.updateTo(this.initVertex.get(y).get(x + 1));
      }
    }
    else if (dir.equals("b")) {
      if (this.isOutOfBounds(y + 1, this.worldHeight)) {
        nextEdge.updateTo(v);
      }
      else {
        nextEdge.updateTo(this.initVertex.get(y + 1).get(x));
      }
    }
    else {
      if (this.isOutOfBounds(x - 1, this.worldWidth)) {
        nextEdge.updateTo(v);
      }
      else {
        nextEdge.updateTo(this.initVertex.get(y).get(x - 1));
      }
    }
  }

  // checks if the given index is out of bounds
  boolean isOutOfBounds(int i, int boundary) {
    return i >= boundary || i < 0;
  }

  // adds all of the initialized verteces to this graph's list of vertex
  void addVertices() {
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
  void updateWalls() {
    this.addVertices();
    ArrayList<Edge> toRemove = /* new ArrayList<Edge>(); */ this.state.kruskal();
    ArrayList<Edge> alreadyChecked = new ArrayList<Edge>();

    for (Vertex v : this.state.allVertex) {
      for (Edge e : v.outEdges) {
        if (!(alreadyChecked.contains(e))) {
          if (toRemove.contains(e)) {
            Vertex adjacentCell = e.to;

            // check direction of connection
            if (e.direction.equals("t") && !(this.isOutOfBounds(v.y - 1, this.worldHeight))) {
              if (!(v.equals(adjacentCell))) {
                v.topEdge = true;
                adjacentCell.bottomEdge = true;
              }
            }
            if (e.direction.equals("r") && !(this.isOutOfBounds(v.x + 1, this.worldWidth))) {
              if (!(v.equals(adjacentCell))) {
                v.rightEdge = true;
                adjacentCell.leftEdge = true;
              }
            }
            if (e.direction.equals("b") && !(this.isOutOfBounds(v.y + 1, this.worldHeight))) {
              if (!(v.equals(adjacentCell))) {
                v.bottomEdge = true;
                adjacentCell.topEdge = true;
              }
            }
            if (e.direction.equals("l") && !(this.isOutOfBounds(v.x - 1, this.worldWidth))) {
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
  void breadthSearch() {
    this.initSearchConditions();
    if (!(this.isAnimating)) {
      this.implementSearch("b");
    }
  }

  // solves the maze using depth search
  void depthSearch() {
    this.initSearchConditions();
    if (!(this.isAnimating)) {
      this.implementSearch("d");
    }
  }

  // implements the search regardless of type
  void implementSearch(String type) {

    while (this.worklist.size() > 0 && this.isSearching) {
      this.nextStep();
    }
  }

  // implements the next step of the search
  @SuppressWarnings("unchecked")
  void nextStep() {

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
        if (((e.direction.equals("t") && next.topEdge)
                || (e.direction.equals("r") && next.rightEdge)
                || (e.direction.equals("l") && next.leftEdge)
                || (e.direction.equals("b") && next.bottomEdge))
                && this.cameFromEdge.get(e.to) == null) {
          this.worklist.add(e.to);
          this.cameFromEdge.put(e.to, next);
        }
      }
    }
    this.alreadyChecked.add(next);
    next.isVisited = true;
  }

  // travels back along the path, labeling each vertex as being part of it
  void reconstruct(HashMap<Vertex, Vertex> hashMap, Vertex v) {
    v.isPath = true;
    if (!(v.x == 0 && v.y == 0)) {
      Vertex next = hashMap.get(v);
      this.reconstruct(hashMap, next);
    }
  }

  // initializes search conditions
  void initSearchConditions() {
    if (this.type.equals("b")) {
      this.worklist = new java.util.LinkedList<Vertex>();
    }
    else if (this.type.equals("d")) {
      this.worklist = new java.util.Stack<Vertex>();
    }
    else {
      throw new RuntimeException("This search type does not exist");
    }
    this.worklist.add(this.state.allVertex.get(0));
  }
}

class ExamplesMaze {

  Vertex A;
  Vertex B;
  Vertex C;
  Vertex D;
  Vertex E;
  Vertex F;
  Vertex G;
  Vertex H;
  Vertex I;

  Edge AB;
  Edge AD;
  Edge BA;
  Edge BC;
  Edge BE;
  Edge CB;
  Edge CF;
  Edge DA;
  Edge DE;
  Edge DG;
  Edge EB;
  Edge ED;
  Edge EF;
  Edge EH;
  Edge FC;
  Edge FE;
  Edge FI;
  Edge GD;
  Edge GH;
  Edge HG;
  Edge HE;
  Edge HI;
  Edge IH;
  Edge IF;

  Graph graph;

  MazeWorld world;

  // intitial conditions
  void initConditions() {

    this.A = new Vertex(0, 0);
    this.B = new Vertex(1, 0);
    this.C = new Vertex(2, 0);
    this.D = new Vertex(0, 1);
    this.E = new Vertex(1, 1);
    this.F = new Vertex(2, 1);
    this.G = new Vertex(0, 2);
    this.H = new Vertex(1, 2);
    this.I = new Vertex(2, 2);

    this.AB = new Edge(4, "r");
    this.AB.updateFrom(this.A);
    this.AB.updateTo(this.B);

    this.AD = new Edge(2, "b");
    this.AD.updateFrom(this.A);
    this.AD.updateTo(this.D);

    this.BA = new Edge(6, "l");
    this.BA.updateFrom(this.B);
    this.BA.updateTo(this.A);

    this.BC = new Edge(8, "r");
    this.BC.updateFrom(this.B);
    this.BC.updateTo(this.C);

    this.BE = new Edge(1, "b");
    this.BE.updateFrom(this.B);
    this.BE.updateTo(this.E);

    this.CB = new Edge(10, "l");
    this.CB.updateFrom(this.C);
    this.CB.updateTo(this.B);

    this.CF = new Edge(9, "b");
    this.CF.updateFrom(this.C);
    this.CF.updateTo(this.F);

    this.DA = new Edge(12, "t");
    this.DA.updateFrom(this.D);
    this.DA.updateTo(this.A);

    this.DE = new Edge(15, "r");
    this.DE.updateFrom(this.D);
    this.DE.updateTo(this.E);

    this.DG = new Edge(3, "b");
    this.DG.updateFrom(this.D);
    this.DG.updateTo(this.G);

    this.ED = new Edge(4, "r");
    this.ED.updateFrom(this.E);
    this.ED.updateTo(this.D);

    this.EB = new Edge(5, "t");
    this.EB.updateFrom(this.E);
    this.EB.updateTo(this.B);

    this.EF = new Edge(2, "r");
    this.EF.updateFrom(this.E);
    this.EF.updateTo(this.F);

    this.EH = new Edge(9, "b");
    this.EH.updateFrom(this.E);
    this.EH.updateTo(this.H);

    this.FE = new Edge(14, "l");
    this.FE.updateFrom(this.F);
    this.FE.updateTo(this.E);

    this.FC = new Edge(2, "t");
    this.FC.updateFrom(this.F);
    this.FC.updateTo(this.C);

    this.FI = new Edge(9, "b");
    this.FI.updateFrom(this.F);
    this.FI.updateTo(this.I);

    this.GD = new Edge(7, "t");
    this.GD.updateFrom(this.G);
    this.GD.updateTo(this.D);

    this.GH = new Edge(5, "r");
    this.GH.updateFrom(this.G);
    this.GH.updateTo(this.H);

    this.HG = new Edge(7, "l");
    this.HG.updateFrom(this.H);
    this.HG.updateTo(this.G);

    this.HE = new Edge(6, "t");
    this.HE.updateFrom(this.H);
    this.HE.updateTo(this.E);

    this.HI = new Edge(13, "r");
    this.HI.updateFrom(this.H);
    this.HI.updateTo(this.I);

    this.IH = new Edge(11, "l");
    this.IH.updateFrom(this.I);
    this.IH.updateTo(this.H);

    this.IF = new Edge(1, "t");
    this.IF.updateFrom(this.I);
    this.IF.updateTo(this.F);

    this.graph = new Graph();

    this.graph.allVertex.add(this.A);
    this.graph.allVertex.add(this.B);
    this.graph.allVertex.add(this.C);
    this.graph.allVertex.add(this.D);
    this.graph.allVertex.add(this.E);
    this.graph.allVertex.add(this.F);
    this.graph.allVertex.add(this.G);
    this.graph.allVertex.add(this.H);
    this.graph.allVertex.add(this.I);

    this.graph.initRepresentatives();

    this.world = new MazeWorld();

  }

  // test the isOutOfBounds method
  void testOutOfBounds(Tester t) {

    // set up initial conditions
    this.initConditions();

    t.checkExpect(this.world.isOutOfBounds(0, 10), false);
    t.checkExpect(this.world.isOutOfBounds(1, 10), false);
    t.checkExpect(this.world.isOutOfBounds(9, 10), false);
    t.checkExpect(this.world.isOutOfBounds(10, 10), true);
    t.checkExpect(this.world.isOutOfBounds(11, 10), true);
    t.checkExpect(this.world.isOutOfBounds(-1, 10), true);
  }

  // test method makeEdges
  void testMakeEdges(Tester t) {

    // set up conditions
    this.initConditions();

    ArrayList<Edge> edgeList = this.graph.makeEdges();

    for (int i = 0; i < edgeList.size(); i++) {
      if (i + 1 < edgeList.size()) {
        t.checkExpect(edgeList.get(i).weight <= edgeList.get(i + 1).weight, true);
      }
    }
  }

  // test the method union
  void testUnion(Tester t) {

    // set up conditions
    this.initConditions();

    this.graph.union(this.A, this.B);

    t.checkExpect(this.graph.representatives.get(this.A), this.B);
    t.checkExpect(this.graph.representatives.get(this.B), this.B);

  }

  // test the method find
  void testFind(Tester t) {

    // set up conditions
    this.initConditions();

    // alter conditions
    this.graph.union(this.A, this.B);
    this.graph.union(this.C, this.A);

    t.checkExpect(this.graph.find(this.C), this.B);
    t.checkExpect(this.graph.find(this.B), this.B);

  }

  // test the method kruskal
  void testKruskal(Tester t) {

    // set up conditions
    this.initConditions();

    // alter conditions
    ArrayList<Edge> kruskal = this.graph.kruskal();

    t.checkExpect(this.graph.allVertex.size(), 9);
    t.checkExpect(kruskal.size(), 8);

    boolean checkAll = true;

    for (Vertex v : this.world.state.allVertex) {
      if (v.x == 0) {
        checkAll = checkAll && t.checkExpect(v.leftEdge, false);
      }
      if (v.y == 0) {
        checkAll = checkAll && t.checkExpect(v.topEdge, false);
      }
      if (v.x == this.world.worldWidth) {
        checkAll = checkAll && t.checkExpect(v.rightEdge, false);
      }
      if (v.y == this.world.worldHeight) {
        checkAll = checkAll && t.checkExpect(v.bottomEdge, false);
      }
    }
  }

  // test the method makeVertices
  void testMakeVertices(Tester t) {

    // set up initial conditions
    this.initConditions();

  }

  // test the method addVertices
  void testAddVertices(Tester t) {
    // set up initial conditions
    this.initConditions();

    t.checkExpect(world.state.allVertex.size(), world.worldHeight * world.worldWidth);
  }

  // testing bigBang()
  void testBigBang(Tester t) {
    this.initConditions();

    this.world.bigBang(this.world.worldWidth * MazeWorld.CELL_SIZE + 10,
            this.world.worldHeight * MazeWorld.CELL_SIZE + 10, .000001);
  }
}