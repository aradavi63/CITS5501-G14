# Verification

## Final run: 2026-10-19 (1 s delay)

The updated source compiled successfully (requires JDK 17 or later).
The live run used the default course policy index, with a 1 second delay
between document attempts.

- 216 distinct candidate URLs discovered.
- Five distinct successful HTTP transfers, all PDFs.
- No failed transfers and no HTTP 429 responses.
- Six HTTP requests total: one index request and five document requests (6/20).
- Successful body sizes: 301201, 163909, 217240, 148249, 136890 bytes.
- All five declared application/pdf and started with 255044462d312e36 (%PDF-1.6).
- Full output is in RUN-OUTPUT.txt.

HTTP transfer success requires status 200 and a non-empty body. No full
document validation was performed; these results do not establish that the
documents are valid or readable. Use these figures in the report.

## Previous run: 2026-10-19 (0.5 s delay)

The HTTP 429 response in this run led us to increase the delay between document attempts to 1 second.

The updated source compiled successfully with JDK 21 (requires JDK 17 or later).
The live run used the default course policy index.

- 216 distinct candidate URLs discovered.
- Five distinct successful HTTP transfers: four PDFs and one Word document.
- One PDF candidate returned HTTP 429 and was not counted; the next candidate succeeded.
- Seven HTTP requests total: one index request and six document attempts (7/20).
- Successful body sizes: 301201, 163909, 217240, 148249, 256512 bytes.
- PDFs declared application/pdf and started with 255044462d312e36 (%PDF-1.6).
- The Word response declared application/msword and started with d0cf11e0a1b11ae1.
- Required metadata and the requested/final document URLs are in RUN-OUTPUT-0.5s.txt.

HTTP transfer success requires status 200 and a non-empty body. Successful final
URLs alone are stored for duplicate tracking. No full document parser validation
was performed; these results do not establish document validity/readability.
This was a normal runtime check, not a deliberate rate-limit experiment for Task 3.

## Previous run: 2026-10-17

The earlier source discovered 216 candidates and retrieved five PDF responses
using six requests. Those figures are historical; use the figures from the final run (1 s delay)
in the report. Website responses can vary between runs.
