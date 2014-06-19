import java.util.Random;
import java.util.LinkedList;

public class Simulate {
    private static double clock;
    private static double meanInterArrivalTime;
    private static double meanServiceTime;
    private static double sigma;
    private static double lastEventTime;
    private static double totalBusy;
    private static double maxQueueLength;
    private static double sumResponseTime;
    private static long queueLength;
    private static long numberInService; // either 0 or 1
    private static long totalCustomers;
    private static long numberOfDepartures;
    private static long longService;
    
    private final static int ARRIVAL = 1;
    private final static int DEPARTURE = 2;
    
    private static LinkedList<Event> futureEventList;
    private static LinkedList<Event> customersQueue;
    private static Random stream;
    
    
    // seed the event list with TotalCustomers arrivals
    public static void initialization() {
        clock = 0.0;
        queueLength = 0;
        numberInService = 0;
        lastEventTime = 0.0;
        totalBusy = 0;
        maxQueueLength = 0;
        sumResponseTime = 0;
        numberOfDepartures = 0;
        longService = 0;
        
        // create first arrival event
        Event evt = new Event(ARRIVAL, exponential(stream, meanInterArrivalTime));
        futureEventList.add(evt);
    }
    
    public static void processArrival(Event evt) {
        customersQueue.add(evt);
        queueLength++;
        
        // if the server is idle, fetch the event, do statistics
        // and put into service
        if (numberInService == 0)
            scheduleDeparture();
        else
            totalBusy += (clock - lastEventTime); // server is busy
        
        // adjust max queue length statistics
        if (maxQueueLength < queueLength)
            maxQueueLength = queueLength;
        
        // schedule the next arrival
        Event nextArrival = new Event(ARRIVAL, clock + exponential(stream, meanInterArrivalTime));
        futureEventList.add(nextArrival);
        lastEventTime = clock;
    }
    
    public static void scheduleDeparture() {
        double ServiceTime;
        ServiceTime = exponential(stream, meanServiceTime);
        
        Event depart = new Event(DEPARTURE, clock + ServiceTime);
        futureEventList.add(depart);
        numberInService = 1;
        queueLength--;
    }
    
    public static void processDeparture(Event e) {
        // get the customer description
        Event finished = (Event) customersQueue.removeFirst();
        
        //System.out.println("d " + e.time);
        
        // measure the response time and add to the sum
        double response = (clock - finished.getTime());
        
        sumResponseTime += response;
        if (response > 4.0)
            longService++; // record long service
        totalBusy += (clock - lastEventTime);
        numberOfDepartures++;
        lastEventTime = clock;
        
        // if there are customers in the queue then schedule
        // the departure of the next one
        if (queueLength > 0)
            scheduleDeparture();
        else
            numberInService = 0;
    }
    
    public static void reportGeneration() {
        double rho = totalBusy / clock;
        double avgr = sumResponseTime / totalCustomers;
        double pc4 = ((double) longService) / totalCustomers;
        
        System.out.println("SINGLE SERVER QUEUE SIMULATION - GROCERY STORE CHECKOUT COUNTER ");
        System.out.println("\tMEAN INTERARRIVAL TIME                         " + meanInterArrivalTime);
        System.out.println("\tMEAN SERVICE TIME                              " + meanServiceTime);
        System.out.println("\tSTANDARD DEVIATION OF SERVICE TIMES            " + sigma);
        System.out.println("\tNUMBER OF CUSTOMERS SERVED                     " + totalCustomers);
        System.out.println();
        System.out.println("\tSERVER UTILIZATION                             " + rho);
        System.out.println("\tMAXIMUM LINE LENGTH                            " + maxQueueLength);
        System.out.println("\tAVERAGE RESPONSE TIME                          " + avgr + "  MINUTES");
        System.out.println("\tPROPORTION WHO SPEND FOUR ");
        System.out.println("\t MINUTES OR MORE IN SYSTEM                     " + pc4);
        System.out.println("\tSIMULATION RUNLENGTH                           " + clock + " MINUTES");
        System.out.println("\tNUMBER OF DEPARTURES                           " + totalCustomers);
    }
    
    public static double exponential(Random rng, double mean) {
        return -mean * Math.log(rng.nextDouble());
    }
    
    public static void main(String argv[]) {
        meanInterArrivalTime = 4.5;
        meanServiceTime = 3.2;
        sigma = 0.6;
        totalCustomers = 1000;
        long seed = 123567;
        //long seed = Long.parseLong(argv[0]);
        
        stream = new Random(seed); // initialize rnd stream
        futureEventList = new LinkedList<Event>();
        customersQueue = new LinkedList<Event>();
        
        initialization();
        
        // Loop until first "TotalCustomers" have departed
        while (numberOfDepartures < totalCustomers) {
            Event evt = (Event) futureEventList.getFirst(); // get imminent event
            futureEventList.removeFirst(); // be rid of it
            clock = evt.getTime(); // advance simulation time
            if (evt.getType() == ARRIVAL)
                processArrival(evt);
            else
                processDeparture(evt);
        }
        reportGeneration();        
    }
    
}