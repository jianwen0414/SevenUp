package hackthefuture;

public class BookingDestination implements Comparable<BookingDestination>{
    String name;
    double x;
    double y;
    double euclidean;
    
    public BookingDestination(String name, double x, double y, Parents parent){
        this.name = name;
        this.x = x;
        this.y = y;
        euclidean = Math.sqrt(Math.pow((this.x-parent.locationCoordinateX), 2) + Math.pow((this.y-parent.locationCoordinateY), 2));
    }
     
    @Override
    public int compareTo(BookingDestination other) {
        if (this.euclidean < other.euclidean) {
            return -1;
        } else if (this.euclidean > other.euclidean) {
            return 1;
        } else {
            return 0;
        }
    }
}
