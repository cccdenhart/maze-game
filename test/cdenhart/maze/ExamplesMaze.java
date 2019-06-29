package cdenhart.maze;

import java.util.ArrayList;
import tester.*;

public class ExamplesMaze {

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
        t.checkExpect(edgeList.get(i).getWeight() <= edgeList.get(i + 1).getWeight(), true);
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
  public static void main(String[] args) {
    ExamplesMaze maze = new ExamplesMaze();
    maze.initConditions();

    maze.world.bigBang(maze.world.worldWidth * MazeWorld.CELL_SIZE + 10,
            maze.world.worldHeight * MazeWorld.CELL_SIZE + 10, .000001);
  }
}