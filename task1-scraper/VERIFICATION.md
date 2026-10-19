# Verification

## Final run: 2026-10-19

The updated source compiled successfully with JDK 21 (requires JDK 17 or later).
The live run used the default course policy index.

- 216 distinct candidate URLs discovered.
- Five distinct successful HTTP transfers: four PDFs and one Word document.
- One PDF candidate returned HTTP 429 and was not counted; the next candidate succeeded.
- Seven HTTP requests total: one index request and six document attempts (7/20).
- Successful body sizes: 301201, 163909, 217240, 148249, 256512 bytes.
- PDFs declared application/pdf and started with 255044462d312e36 (%PDF-1.6).
- The Word response declared application/msword and started with d0cf11e0a1b11ae1.
- Required metadata and the requested/final document URLs are in RUN-OUTPUT.txt.

HTTP transfer success requires status 200 and a non-empty body. Successful final
URLs alone are stored for duplicate tracking. No full document parser validation
was performed; these results do not establish document validity/readability.
This was a normal runtime check, not a deliberate rate-limit experiment for Task 3.

## Previous run: 2026-10-17

The earlier source discovered 216 candidates and retrieved five PDF responses
using six requests. Those figures are historical; use the final-run figures above
in the report. Website responses can vary between runs.
