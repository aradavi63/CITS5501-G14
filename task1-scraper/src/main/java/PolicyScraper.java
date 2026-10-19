import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/** Minimal Phase 2 Task 1 prototype. Downloads are kept in memory, not saved. */
public class PolicyScraper {
    private static final String DEFAULT_INDEX =
            "https://uwa-policy.cits5501.arranstewart.io/policy/";
    private static final int MAX_REQUESTS = 20;
    private static final int TARGET_DOCUMENTS = 5;
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            // Redirects are handled explicitly so each HTTP request is counted.
            .followRedirects(HttpClient.Redirect.NEVER).build();
    private int requests;

    public static void main(String[] args) throws Exception {
        URI index = URI.create(args.length == 0 ? DEFAULT_INDEX : args[0]);
        if (!isHttp(index)) {
            throw new IllegalArgumentException("Index must be an HTTP(S) URL");
        }
        new PolicyScraper().run(index);
    }

    private void run(URI index) throws Exception {
        HttpResponse<byte[]> page = fetch(index, index);
        if (page.statusCode() != 200) {
            throw new IllegalStateException("Index returned HTTP " + page.statusCode());
        }
        Set<URI> candidates = new LinkedHashSet<>();
        String html = new String(page.body(), java.nio.charset.StandardCharsets.UTF_8);
        for (Element link : Jsoup.parse(html, page.uri().toString()).select("a[href]")) {
            try {
                URI url = URI.create(link.absUrl("href"));
                // Remove fragments; keep queries, which can affect the downloaded file.
                String text = url.toString();
                int hash = text.indexOf('#');
                url = URI.create(hash < 0 ? text : text.substring(0, hash)).normalize();
                if (sameOrigin(index, url) && isDocument(url)) candidates.add(url);
            } catch (IllegalArgumentException ignored) {
                // A malformed link must not stop discovery of other candidates.
            }
        }
        System.out.println("Distinct candidate URLs: " + candidates.size());
        int downloaded = 0;
        Set<URI> finalUrls = new LinkedHashSet<>();
        for (URI url : candidates) {
            if (downloaded >= TARGET_DOCUMENTS || requests >= MAX_REQUESTS) break;
            // A short pause avoids sending a burst of document requests.
            Thread.sleep(500);
            System.out.println("\nPolicy URL: " + url);
            try {
                HttpResponse<byte[]> response = fetch(url, index);
                byte[] body = response.body();
                String type = response.headers().firstValue("Content-Type").orElse("unknown");
                boolean success = response.statusCode() == 200 && body.length > 0;
                boolean distinct = !finalUrls.contains(response.uri());
                if (success && distinct) {
                    finalUrls.add(response.uri());
                    downloaded++;
                }
                System.out.println("Final URL: " + response.uri());
                System.out.println("HTTP status: " + response.statusCode());
                System.out.println("Content-Type: " + type);
                System.out.println("Downloaded bytes: " + body.length);
                System.out.println("Initial bytes (hex): " +
                        java.util.HexFormat.of().formatHex(body, 0, Math.min(8, body.length)));
                System.out.println("HTTP transfer: " + (success ? "SUCCESS" : "FAILED")
                        + (distinct ? "" : " (duplicate final URL)"));
                // HTTP success does not prove the bytes form a valid PDF/Office file.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } catch (Exception e) {
                System.out.println("HTTP transfer FAILED: " + e.getMessage());
            }
        }
        System.out.printf("%nSummary: %d distinct successful HTTP transfers; %d/%d requests%n",
                downloaded, requests, MAX_REQUESTS);
        if (downloaded < 3) {
            System.out.println("Fewer than 3 HTTP transfers succeeded; inspect the failures above.");
        }
    }

    private HttpResponse<byte[]> fetch(URI url, URI origin) throws Exception {
        Set<URI> visited = new LinkedHashSet<>();
        while (true) {
            if (!sameOrigin(origin, url)) throw new IllegalStateException("External redirect refused");
            if (!visited.add(url)) throw new IllegalStateException("Redirect loop");
            if (requests >= MAX_REQUESTS) throw new IllegalStateException("Request limit reached");
            HttpRequest request = HttpRequest.newBuilder(url).timeout(Duration.ofSeconds(20))
                    .header("User-Agent", "CITS5501-Task1-Prototype/1.0").GET().build();
            requests++;
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            int status = response.statusCode();
            if (status == 301 || status == 302 || status == 303 || status == 307 || status == 308) {
                String location = response.headers().firstValue("Location")
                        .orElseThrow(() -> new IllegalStateException("Redirect without Location"));
                url = url.resolve(location);
            } else {
                return response;
            }
        }
    }

    private static boolean isDocument(URI url) {
        String path = url.getPath();
        return path != null && path.toLowerCase(Locale.ROOT)
                .matches(".*\\.(pdf|doc|docx|xls|xlsx|ppt|pptx|odt|ods|odp|rtf)$");
    }

    private static boolean isHttp(URI url) {
        return url.getHost() != null && ("http".equalsIgnoreCase(url.getScheme())
                || "https".equalsIgnoreCase(url.getScheme()));
    }

    private static boolean sameOrigin(URI a, URI b) {
        return isHttp(b) && a.getScheme().equalsIgnoreCase(b.getScheme())
                && a.getHost().equalsIgnoreCase(b.getHost()) && port(a) == port(b);
    }

    private static int port(URI url) {
        return url.getPort() != -1 ? url.getPort()
                : ("https".equalsIgnoreCase(url.getScheme()) ? 443 : 80);
    }
}
