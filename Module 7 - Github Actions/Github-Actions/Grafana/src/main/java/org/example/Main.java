package org.example;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;

public class Main {

    // Range of random values to be inserted and removed from BST
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 300;

    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {

        // Create a counter to track the number of failed removes
        Counter failedRemoves = Counter.build()
                .namespace("java")
                .name("failed_removes")
                .help("Counts the number of times the server failed to remove a number")
                .register();

        // Create a counter to track the number of failed adds
        Counter failedAdds = Counter.build()
                .namespace("java")
                .name("failed_adds")
                .help("Counts the number of times the server failed to add a number")
                .register();

        // Create a gauge to track the number of nodes currently on the server
        Gauge numberOfNodes = Gauge.build()
                .namespace("java")
                .name("number_of_nodes")
                .help("Describes the number of BST nodes currently on the server")
                .register();

        // Create a Summary metric to track the amount of time it takes for the BST add method to run
        Summary addSummary = Summary.build()
                .namespace("java")
                .name("add_time_summary")
                .help("A summary of the time it takes for the BST add method to run")
                .register();

        BST<Integer> tree = new BST<>();

        // Define the thread that contains all the processes that the server runs through
        Thread bgThread = new Thread(() -> {
            while (true) {
                try {

                    // Two random values to insert or remove from the BST are selected
                    int randomAdd = randomNumber();
                    int randomRemove = randomNumber();

                    /*
                     The server attempts to add the random value.
                     Returns true if the server added a node in the BST.
                     Returns false if a node was not added.
                     */
                    // Start the Summary timer before the add method is called
                    Summary.Timer addTimer;
                    addTimer = addSummary.startTimer();

                    // Call the add method
                    if(!tree.add(randomAdd)){
                        failedAdds.inc();
                    } else {
                        numberOfNodes.inc();
                    }
                    // Observe the amount of time it took for the add method to run
                    addTimer.observeDuration();

                    /*
                    The server attempts to remove the random given value
                    IF IT FAILS, it will throw a FailedRemoveException
                    IF IT SUCCEEDS it won't throw any exception
                    */
                    try{
                        tree.remove(randomRemove);
                        numberOfNodes.dec();
                    }catch (FailedRemoveException e){
                        failedRemoves.inc();
                    }

                    // The server waits for 1000 milliseconds
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        bgThread.start();

        // start the server on the specified port
        try {
            HTTPServer server = new HTTPServer(SERVER_PORT);
            System.out.println("Prometheus exporter running on port " + SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Prometheus exporter was unable to start");
            e.printStackTrace();
        }
    }

    /**
     * Function that selects a random number.
     * @return the randomly selected integer between MIN_VALUE and MAX_VALUE
     */
    private static int randomNumber() {
        return (int)(Math.random() * ((MAX_VALUE - MIN_VALUE) + 1)) + MIN_VALUE;
    }

}