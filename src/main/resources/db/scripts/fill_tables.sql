INSERT INTO MANUFACTURER (MANUFACTURER_NAME, IS_CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/manufacturers.csv');

INSERT INTO MANUFACTURER (MANUFACTURER_NAME, IS_CUSTOM)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/manufacturers_custom.csv');

INSERT INTO INJECTOR_TYPE (TYPE_NAME, INJECTOR_TYPE)
  SELECT * FROM CSVREAD('classpath:/db/csv/injectorTypes.csv');

INSERT INTO VOLT_AMPERE_PROFILE (PROFILE_NAME, INJECTOR_TYPE, IS_CUSTOM, BOOST_U, BATTERY_U
  , BOOST_I, FIRST_I, FIRST_W, SECOND_I, NEGATIVE_U, BOOST_DISABLE)
  SELECT * FROM CSVREAD('classpath:/db/csv/voltAmpereProfiles.csv');

INSERT INTO VOLT_AMPERE_PROFILE (PROFILE_NAME, INJECTOR_TYPE, IS_CUSTOM, BOOST_U, BATTERY_U
  , BOOST_I, FIRST_I, FIRST_W, SECOND_I, NEGATIVE_U, BOOST_DISABLE)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/voltAmpereProfiles_custom.csv');

INSERT INTO INJECTOR(INJECTOR_CODE, MANUFACTURER, VOLT_AMPERE_PROFILE, CODE_TYPE, CALIBRATION_ID, COEFFICIENT, IS_CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/injectors.csv');

INSERT INTO INJECTOR(INJECTOR_CODE, MANUFACTURER, VOLT_AMPERE_PROFILE, CODE_TYPE, CALIBRATION_ID, COEFFICIENT, IS_CUSTOM)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/injectors_custom.csv');

INSERT INTO TEST_NAME (ID, TEST_NAME, MEASUREMENT, DISPLAY_ORDER)
  SELECT * FROM CSVREAD('classpath:/db/csv/testNames.csv');

INSERT INTO ISA_DETECTION (INJECTOR_CODE, MASK_TYPE, SHIFT_VOLTAGE, CHAR_0, CHAR_1, CHAR_2, CHAR_3, CHAR_4, CHAR_5, CHAR_6, CHAR_7, CHAR_8, CHAR_9, CHAR_10, CHAR_11, CHAR_12, CHAR_13, CHAR_14, CHAR_15)
  SELECT * FROM CSVREAD('classpath:/db/csv/isaDetection.csv');

INSERT INTO INJECTOR_TEST (ID, INJECTOR_CODE, TEST_NAME, MOTOR_SPEED, SETTED_PRESSURE, ADJUSTING_TIME,
MEASUREMENT_TIME, INJECTION_RATE, TOTAL_PULSE_TIME, NOMINAL_FLOW, FLOW_RANGE, VOLT_AMPERE_PROFILE)
  SELECT * FROM CSVREAD('classpath:/db/csv/injectorTests.csv');

INSERT INTO INJECTOR_TEST (ID, INJECTOR_CODE, TEST_NAME, MOTOR_SPEED, SETTED_PRESSURE, ADJUSTING_TIME,
MEASUREMENT_TIME, INJECTION_RATE, TOTAL_PULSE_TIME, NOMINAL_FLOW, FLOW_RANGE, VOLT_AMPERE_PROFILE)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/injectorTests_custom.csv');

INSERT INTO PARAMETERS (INJECTOR_CODE, ADDITIONAL_SERIAL_NUMBER,AH,RLS,AHE,DNH,UEH,IMAGE)
  SELECT * FROM CSVREAD('classpath:/db/csv/bosch/parameters.csv');

INSERT INTO NOZZLES (ID, INJECTOR_CODE, NOZZLE_CODE)
  SELECT * FROM CSVREAD('classpath:/db/csv/bosch/nozzles.csv');

INSERT INTO VALVES (ID, INJECTOR_CODE, VALVE_CODE)
  SELECT * FROM CSVREAD('classpath:/db/csv/bosch/valves.csv');

INSERT INTO APPLICATIONS (ID, INJECTOR_CODE, APPLICATION)
  SELECT * FROM CSVREAD('classpath:/db/csv/bosch/application.csv');

INSERT INTO ENGINES (ENGINE, ENGINE_TYPE)
  SELECT * FROM CSVREAD('classpath:/db/csv/siemens/siemensEngines.csv');

INSERT INTO CARS (ID, ENGINE, CAR)
  SELECT * FROM CSVREAD('classpath:/db/csv/siemens/siemensCars.csv');

INSERT INTO SPARES (ID, ENGINE, CATEGORY, ORDER_NUMBER, DESCRIPTION)
  SELECT * FROM CSVREAD ('classpath:/db/csv/siemens/siemensSpareParts.csv');

INSERT INTO REFERENCE (INJECTOR, DESCRIPTION, ENGINE, ORDER_NUMBER, REMARKS)
  SELECT * FROM CSVREAD('classpath:/db/csv/siemens/siemensCrossReference.csv');

INSERT INTO DENSOINJECTORS(ID, CODE1, DESCRIPTION, CODE2)
  SELECT * FROM CSVREAD('classpath:/db/csv/denso/densoInjectors.csv')