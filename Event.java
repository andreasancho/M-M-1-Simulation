/**
 * A simple event class consisting of only two attributes
 */
class Event {
    private double time;
    private int type;
    
    public Event(int tp, double tm) {
        this.time = tm;
        this.type = tp;
    }
    
    public int getType() {
        return this.type;
    }
    
    public double getTime() {
        return this.time;
    }
}// Event