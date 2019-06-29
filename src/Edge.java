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