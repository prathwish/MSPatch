CREATE TABLE code_value
(
   CODE_VALUE_ID        BIGINT(8) NOT NULL AUTO_INCREMENT PRIMARY KEY,
   CODE_TABLE_ID        BIGINT(8) NOT NULL,
   FOREIGN KEY 		(CODE_TABLE_ID) 
	REFERENCES code_table (CODE_TABLE_ID) 
   	ON DELETE RESTRICT
    	ON UPDATE RESTRICT,
   SHORT_NAME           VARCHAR (10) NOT NULL,
   LONG_NAME            VARCHAR (255),
   EFFECTIVE_DATE		DATE,
   BPA_AUDIT_TRAIL_ID   BIGINT(8),
   AUDIT_USER           VARCHAR (40) NOT NULL,
   AUDIT_LDAP_DN        VARCHAR (200),
   AUDIT_TIMESTAMP      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX XAK02CODE_VALUE1
  ON code_value
    ( CODE_TABLE_ID ASC, SHORT_NAME ASC );

CREATE INDEX XIE02CODE_VALUE02
  ON code_value
    ( SHORT_NAME ASC );
  
CREATE INDEX XIE02CODE_VALUE03
  ON code_value
    ( LONG_NAME ASC );
  
CREATE INDEX XIE02CODE_VALUE04
  ON code_value
    ( BPA_AUDIT_TRAIL_ID ASC );