ALTER TABLE GRAD_SCHOOL_HISTORY
    RENAME COLUMN SUBMISSION_MODE TO SUBMISSION_MODE_CODE;

ALTER TABLE GRAD_SCHOOL_HISTORY
    RENAME COLUMN TRANSCRIPT_ELIGIBILITY TO CAN_ISSUE_TRANSCRIPTS;

ALTER TABLE GRAD_SCHOOL_HISTORY
    RENAME COLUMN CERTIFICATE_ELIGIBILITY TO CAN_ISSUE_CERTIFICATES;
