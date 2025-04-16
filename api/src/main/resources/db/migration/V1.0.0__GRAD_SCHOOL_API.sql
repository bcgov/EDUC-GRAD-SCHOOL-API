CREATE TABLE GRAD_SCHOOL
(
    GRAD_SCHOOL_ID                      RAW(16) NOT NULL,
    SCHOOL_ID                           RAW(16) NOT NULL,
    SUBMISSION_MODE_CODE                VARCHAR2(10) NOT NULL,
    CAN_ISSUE_TRANSCRIPTS               CHAR(1) NOT NULL,
    CAN_ISSUE_CERTIFICATES              CHAR(1) NOT NULL,
    CREATE_USER                         VARCHAR2(32)         NOT NULL,
    CREATE_DATE                         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATE_USER                         VARCHAR2(32)         NOT NULL,
    UPDATE_DATE                         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT GRAD_SCHOOL_ID_PK PRIMARY KEY (GRAD_SCHOOL_ID)
);

CREATE TABLE GRAD_SCHOOL_HISTORY
(
    GRAD_SCHOOL_HISTORY_ID              RAW(16) NOT NULL,
    GRAD_SCHOOL_ID                      RAW(16) NOT NULL,
    SCHOOL_ID                           RAW(16) NOT NULL,
    SUBMISSION_MODE                     VARCHAR2(10) NOT NULL,
    TRANSCRIPT_ELIGIBILITY              CHAR(1) NOT NULL,
    CERTIFICATE_ELIGIBILITY             CHAR(1) NOT NULL,
    CREATE_USER                         VARCHAR2(32)         NOT NULL,
    CREATE_DATE                         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATE_USER                         VARCHAR2(32)         NOT NULL,
    UPDATE_DATE                         TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT GRAD_SCHOOL_HISTORY_ID_PK PRIMARY KEY (GRAD_SCHOOL_HISTORY_ID)
);

-------------------------------------------------CODE TABLES--------------------------------

CREATE TABLE SUBMISSION_MODE_CODE
(
    SUBMISSION_MODE_CODE              VARCHAR2(10)           NOT NULL,
    LABEL                             VARCHAR2(30) NOT NULL,
    DESCRIPTION                       VARCHAR2(255) NOT NULL,
    DISPLAY_ORDER                     NUMBER DEFAULT 1       NOT NULL,
    EFFECTIVE_DATE                    TIMESTAMP                   NOT NULL,
    EXPIRY_DATE                       TIMESTAMP                   NOT NULL,
    CREATE_USER                       VARCHAR2(32)           NOT NULL,
    CREATE_DATE                       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UPDATE_USER                       VARCHAR2(32)           NOT NULL,
    UPDATE_DATE                       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT SUBMISSION_MODE_CODE_PK PRIMARY KEY (SUBMISSION_MODE_CODE)
);


--School & School History Foreign Keys
ALTER TABLE GRAD_SCHOOL
    ADD CONSTRAINT FK_GRAD_SCHOOL_SUBM_MODE_CODE FOREIGN KEY (SUBMISSION_MODE_CODE) REFERENCES SUBMISSION_MODE_CODE (SUBMISSION_MODE_CODE),

ALTER TABLE GRAD_SCHOOL
    ADD CONSTRAINT FK_GRAD_SCHOOL_SUBM_MODE_CODE FOREIGN KEY (SUBMISSION_MODE_CODE) REFERENCES SUBMISSION_MODE_CODE (SUBMISSION_MODE_CODE);

ALTER TABLE GRAD_SCHOOL
    ADD CONSTRAINT FK_GRAD_SCHL_HIST_HIST_SCHL_ID FOREIGN KEY (GRAD_SCHOOL_ID) REFERENCES GRAD_SCHOOL (GRAD_SCHOOL_ID);



