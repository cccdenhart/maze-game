package cdenhart.maze;

import java.util.ArrayList;
import java.util.HashMap;

// represents the player
public class Player {

  private int x;
    private int y;
    private ArrayList<Vertex> vertices;
    private Vertex curVert = new Vertex(0, 0);
    private HashMap<Vertex, Vertex> cameFromEdge;
  
    public Player(ArrayList<Vertex> vertices, HashMap<Vertex, Vertex> cameFromEdge) {
      this.x = 0;
      this.y = 0;
      this.vertices = vertices;
      this.updateVertex();
      this.cameFromEdge = cameFromEdge;
    }
  
    // moves the player on arrow key input
    void movePlayer(String ke) {
      switch (ke) {
        case "left":
          if (this.curVert.leftEdge) {
            this.x--;
          }
          break;
        case "right":
          if (this.curVert.rightEdge) {
            this.x++;
          }
          break;
        case "up":
          if (this.curVert.topEdge) {
            this.y--;
          }
          break;
        case "down":
          if (this.curVert.bottomEdge) {
            this.y++;
          }
          break;
      }
      this.updateVertex();
    }
  
    // returns the vertex that the player is on
    private void updateVertex() {
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
    private void updateMap() {
      for (Edge e : this.curVert.outEdges) {
        if (((e.getDirection().equals("t") && this.curVert.topEdge)
                || (e.getDirection().equals("r") && this.curVert.rightEdge)
                || (e.getDirection().equals("l") && this.curVert.leftEdge)
                || (e.getDirection().equals("b") && this.curVert.bottomEdge))
                && this.cameFromEdge.get(e.getTo()) == null) {
          this.cameFromEdge.put(e.getTo(), this.curVert);
        }
      }
    }

    // getter for x
    public int getX() {
      return x;
    }

    // getter for y
    public int getY() {
      return y;
    }
  }