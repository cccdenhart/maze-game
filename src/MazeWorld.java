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