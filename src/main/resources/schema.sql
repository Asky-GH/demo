DROP TABLE IF EXISTS TBL_CLIENTS;

CREATE TABLE TBL_CLIENTS(
    id IDENTITY PRIMARY KEY,
    risk_profile VARCHAR(6) NOT NULL
);