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