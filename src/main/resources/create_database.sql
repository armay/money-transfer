-- Accounts

CREATE SEQUENCE IF NOT EXISTS ACCOUNT_ID_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS ACCOUNT (
  ID BIGINT,
  NAME NVARCHAR(255) NOT NULL,
  PHONE VARCHAR(20) NOT NULL,
  PRIMARY_PAN VARCHAR(19),
  CONSTRAINT ACCOUNT_PK PRIMARY KEY (ID)
);

CREATE UNIQUE INDEX IF NOT EXISTS ACCOUNT_UK_PHONE ON ACCOUNT(PHONE);
CREATE UNIQUE INDEX IF NOT EXISTS ACCOUNT_UK_PRIMARY_PAN ON ACCOUNT(PRIMARY_PAN);

-- Cards

CREATE TABLE IF NOT EXISTS CARD (
  PAN VARCHAR(19) NOT NULL,
  BALANCE NUMBER(18, 2) NOT NULL,
  ACCOUNT_ID BIGINT NOT NULL,
  CONSTRAINT CARD_PK PRIMARY KEY (PAN),
  CONSTRAINT CARD_FK_ACCOUNT FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ID)
);

ALTER TABLE ACCOUNT ADD CONSTRAINT IF NOT EXISTS ACCOUNT_FK_PRIMARY_PAN FOREIGN KEY (PRIMARY_PAN) REFERENCES CARD(PAN);

-- Events

CREATE TABLE IF NOT EXISTS EVENT (
  ID VARCHAR(40),
  PAN VARCHAR(19) NOT NULL,
  VALUE NUMBER(18, 2) NOT NULL,
  DESCRIPTION VARCHAR(255) NOT NULL,
  CREATED_AT TIMESTAMP WITH TIME ZONE NOT NULL,
  TRANSFER_ID VARCHAR(40) NOT NULL,
  CONSTRAINT EVENT_PK PRIMARY KEY (ID),
  CONSTRAINT EVENT_FK_CARD FOREIGN KEY (PAN) REFERENCES CARD(PAN)
);

CREATE INDEX IF NOT EXISTS EVENT_IDX_TRANSFER_ID ON EVENT(TRANSFER_ID);