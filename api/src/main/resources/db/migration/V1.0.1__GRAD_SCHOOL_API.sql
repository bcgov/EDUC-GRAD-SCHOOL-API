INSERT INTO SUBMISSION_MODE_CODE (SUBMISSION_MODE_CODE, LABEL, DESCRIPTION,
                                       DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE, CREATE_USER,
                                       CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES ('REPLACE', 'Replace', 'Replace mode', 10, TO_DATE('19880930', 'YYYYMMDD'),
        TO_DATE('99991231', 'YYYYMMDD'), 'API_GRAD_SCHOOL',TO_DATE('20250416', 'YYYYMMDD'), 'API_GRAD_SCHOOL',
        TO_DATE('20250416', 'YYYYMMDD'));

INSERT INTO SUBMISSION_MODE_CODE (SUBMISSION_MODE_CODE, LABEL, DESCRIPTION,
                                  DISPLAY_ORDER, EFFECTIVE_DATE, EXPIRY_DATE, CREATE_USER,
                                  CREATE_DATE, UPDATE_USER, UPDATE_DATE)
VALUES ('APPEND', 'Append', 'Append mode', 20, TO_DATE('19880930', 'YYYYMMDD'),
        TO_DATE('99991231', 'YYYYMMDD'), 'API_GRAD_SCHOOL',
        TO_DATE('20250416', 'YYYYMMDD'), 'API_GRAD_SCHOOL',
        TO_DATE('20250416', 'YYYYMMDD'));


