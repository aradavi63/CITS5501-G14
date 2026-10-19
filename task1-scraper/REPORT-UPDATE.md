# Task 1 report update

Insert the following paragraph into Section 1.1 (Download Selected Documents):

A download is counted as an HTTP success only when the server returns status 200 and the response body is non-empty. This confirms successful HTTP retrieval but does not guarantee that the downloaded document is valid or readable. Only successful final URLs are added to the duplicate-tracking set, so a failed transfer does not prevent a later successful transfer to the same final URL from being counted.

Replace any previous runtime figures with the following observations:

The updated prototype was compiled and run against the supplied policy index on 19 October 2026. It discovered 216 distinct candidate URLs and completed five distinct successful HTTP transfers: four PDF responses and one Word document response. One PDF candidate returned HTTP 429 and was not counted as successful; the prototype continued to the next candidate. It used seven HTTP requests in total, including the index request, remaining within the 20-request limit. The output included requested and final URLs, HTTP status, declared content type, downloaded size and initial bytes. These observations establish HTTP retrieval results, not full document validity or readability.

See VERIFICATION.md and RUN-OUTPUT.txt for the recorded results. The team's report file was not provided here; these paragraphs must be incorporated into that report before submission.
