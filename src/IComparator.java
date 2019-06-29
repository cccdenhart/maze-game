// Compares two generic items
interface IComparator<T> {

  // Returns a negative number if t1 comes before t2 in this order
  // Returns zero if t1 is tied with r2 in this order
  // Returns a positive number if t1 comes after t2 in this order
  int compare(T t1, T t2);
}