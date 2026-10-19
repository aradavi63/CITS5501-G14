# Phase 2 Task 1: policy scraper prototype

## Requirements
JDK 17 or later and Jsoup 1.23.2 (`lib/jsoup-1.23.2.jar`).
Jsoup official download: https://jsoup.org/download

## Compile and run (PowerShell, from this directory)
```powershell
New-Item -ItemType Directory -Force build | Out-Null
javac -encoding UTF-8 -cp "lib/jsoup-1.23.2.jar" -d build src/main/java/PolicyScraper.java
java -cp "build;lib/jsoup-1.23.2.jar" PolicyScraper
```
On Linux/macOS use `:` instead of `;` in the runtime classpath.
An optional argument selects a local fixture index, for example:
```powershell
java -cp "build;lib/jsoup-1.23.2.jar" PolicyScraper http://localhost:3000/
```

## Behaviour
Fetches the supplied policy index once and extracts anchor URLs with Jsoup.
Resolves relative links, removes fragments, filters by document extension and
same origin, and removes duplicate URLs. Does not recursively crawl.
Attempts candidates until five distinct final URLs have returned nonempty HTTP
200 bodies, candidates run out, or the request budget is exhausted.
The maximum is 20 HTTP requests including the index, redirects and failed sends.
Requests are sequential, with a 500 ms pause before each document attempt.
Redirects are explicit, counted, and restricted to the original origin.
Outputs requested/final URL, status, declared content type, byte count and the
first eight bytes in hex. Bodies are discarded after processing.

## Assumptions and limitations
- Relevant documents are linked directly by anchors on the index and have a
  recognised extension; extensionless endpoints and JavaScript links are missed.
- The index is decoded as UTF-8. Other encodings are not detected.
- HTTP SUCCESS means nonempty HTTP 200, not a validated office/PDF document.
  HTML returned as a PDF or a corrupt file may still be counted as successful.
- Distinctness uses URLs, not content hashes. Different URLs with identical
  contents can count separately; identical final redirect URLs count once.
- Downloads are buffered in memory without a size cap. Large or slowly streamed
  bodies need additional protection in a production implementation.
- No retries, backoff or complete rate-limit handling; basic errors are printed.
- Same-origin filtering may omit legitimate documents on external hosts.

The scraper's execution results, including discovered document URLs, successful downloads, and HTTP request counts, are documented in VERIFICATION.md.
