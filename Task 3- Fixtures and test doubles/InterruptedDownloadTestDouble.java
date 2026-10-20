/*
 This program creates a fake local HTTP server to simulate an incomplete PDF download.

 How to run:
 STEP 1. Compile javac InterruptedDownloadTestDouble.java
 STEP 2. Run java InterruptedDownloadTestDouble
 STEP 3. Open another terminal and run:
     curl --max-time 5 http://localhost:8080/FakePdf.pdf -o FakePdf.pdf
 
 Expected result:
 The client receives incomplete PDF data and times out.

 Stop the server using Ctrl + C.
 */
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.io.OutputStream;

public class InterruptedDownloadTestDouble {
    public static void main(String[] args)
            throws Exception {

        // Creating fake local HTTP server on port 8080

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);

        // Endpoint for fake PDF download

        server.createContext("/FakePdf.pdf", exchange -> {
            byte[] content = "%PDF-1.4\nTesting interrupted download with test double".getBytes();

            exchange.getResponseHeaders().set("Content-Type", "application/pdf");

            exchange.sendResponseHeaders(200, content.length + 1000);

            // Trying to send incomplete PDF data to simulate a failed download
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(content);
                output.flush();
            } catch (Exception e) {
                System.out.println("Connection closed: " + e.getMessage());
            }

            System.out.println("Simulated interrupted PDF download.");
        });

        // Starting server
        server.start();

        System.out.println("Fake server running at http://localhost:8080/FakePdf.pdf");
    }
}
