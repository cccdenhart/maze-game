package cdenhart.maze;
import java.awt.Color;
import java.util.ArrayList;
import javalib.impworld.WorldScene;
import javalib.worldimages.LineImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

// represents a single vertex
public class Vertex {
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

    public Vertex(int x, int y) {
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