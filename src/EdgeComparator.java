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