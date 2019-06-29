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