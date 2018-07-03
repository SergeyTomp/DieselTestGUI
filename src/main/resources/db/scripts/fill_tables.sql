INSERT INTO MANUFACTURER (MANUFACTURER, IS_CUSTOM)
  SELECT *
  FROM CSVREAD('classpath:/db/csv/manufacturers.csv');

INSERT INTO INJECTOR_TYPE (ID, INJECTOR_TYPE)
  SELECT *
  FROM CSVREAD('classpath:/db/csv/injectorTypes.csv');

INSERT INTO VOLT_AMPERE_PROFILE (PROFILE_NAME, INJECTOR_TYPE, IS_CUSTOM, BOOST_U, BATTERY_U
  , BOOST_I, FIRST_I, FIRST_W, SECOND_I, NEGATIVE_U, BOOST_DISABLE)
  SELECT *
  FROM CSVREAD('classpath:/db/csv/voltAmpereProfiles.csv');

INSERT INTO INJECTOR(INJECTOR_CODE, MANUFACTURER, VOLT_AMPERE_PROFILE, CODETYPE, CALIBRATION_ID, CHECKSUM_M, K_COEFFICIENT, IS_CUSTOM)
    SELECT * FROM CSVREAD('classpath:/db/csv/injectors.csv');

-- INSERT INTO INJECTOR_TYPE (ID, INJECTOR_TYPE)
--   SELECT *
--   FROM CSVREAD('classpath:/db/csv/injectorTypes.csv');
--
-- INSERT INTO VOLT_AMPERE_PROFILE (PROFILE_NAME, VOLTAGE, FIRST_CURRENT, SECOND_CURRENT, FIRST_WIDTH, USER_TYPE, BOOST_U, BOOST_I, BATTERY_U
--   , FIRST_I, FIRST_W, SECOND_I, NEGATIVE_U1, NEGATIVE_U2, BOOST_DISABLE)
--   SELECT *
--   FROM CSVREAD('classpath:/db/csv/voltAmpereProfiles.csv');
--
-- INSERT INTO INJECTOR_CR (INJECTOR_CODE, MANUFACTURER, INJECTOR_TYPE, VOLT_AMPERE_PROFILE, CODETYPE, CALIBRATION_ID, CHECKSUM_M, K_COEFFICIENT)
--   SELECT *
--   FROM CSVREAD('classpath:/db/csv/injectors.csv');
