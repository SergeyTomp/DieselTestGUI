INSERT INTO MANUFACTURER (MANUFACTURER_NAME, IS_CUSTOM, COMMONRAIL, HEUI)
  SELECT * FROM CSVREAD('classpath:/db/csv/cr/manufacturers.csv');

INSERT INTO MANUFACTURER (MANUFACTURER_NAME, IS_CUSTOM, COMMONRAIL, HEUI)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/manufacturers_custom.csv');

INSERT INTO INJECTOR_TYPE (TYPE_NAME, INJECTOR_TYPE)
  SELECT * FROM CSVREAD('classpath:/db/csv/cr/injectorTypes.csv');

INSERT INTO VOLT_AMPERE_PROFILE (PROFILE_NAME, INJECTOR_TYPE, IS_CUSTOM, BOOST_U, BATTERY_U
  , BOOST_I, FIRST_I, FIRST_W, SECOND_I, NEGATIVE_U, BOOST_DISABLE, IS_DOUBLE_COIL, BOOST_I2, FIRST_I2, FIRST_W2, SECOND_I2)
  SELECT * FROM CSVREAD('classpath:/db/csv/cr/voltAmpereProfiles.csv');

INSERT INTO VOLT_AMPERE_PROFILE (PROFILE_NAME, INJECTOR_TYPE, IS_CUSTOM, BOOST_U, BATTERY_U
  , BOOST_I, FIRST_I, FIRST_W, SECOND_I, NEGATIVE_U, BOOST_DISABLE)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/voltAmpereProfiles_custom.csv');

INSERT INTO INJECTOR(INJECTOR_CODE, MANUFACTURER, VOLT_AMPERE_PROFILE, CODE_TYPE, CALIBRATION_ID, COEFFICIENT, IS_CUSTOM, IS_HEUI)
  SELECT * FROM CSVREAD('classpath:/db/csv/cr/injectors.csv');

INSERT INTO INJECTOR(INJECTOR_CODE, MANUFACTURER, VOLT_AMPERE_PROFILE, CODE_TYPE, CALIBRATION_ID, COEFFICIENT, IS_CUSTOM, IS_HEUI)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/injectors_custom.csv');

INSERT INTO TEST_NAME (ID, TEST_NAME, MEASUREMENT,TEST_POINT, DISPLAY_ORDER)
  SELECT * FROM CSVREAD('classpath:/db/csv/cr/testNames.csv');

INSERT INTO ISA_DETECTION (INJECTOR_CODE, MASK_TYPE, SHIFT_VOLTAGE, CHAR_0, CHAR_1, CHAR_2, CHAR_3, CHAR_4, CHAR_5, CHAR_6, CHAR_7, CHAR_8, CHAR_9, CHAR_10, CHAR_11, CHAR_12, CHAR_13, CHAR_14, CHAR_15)
  SELECT * FROM CSVREAD('classpath:/db/csv/cr/isaDetection.csv');

INSERT INTO INJECTOR_TEST (ID, INJECTOR_CODE, TEST_NAME, MOTOR_SPEED, SETTED_PRESSURE, ADJUSTING_TIME,
MEASUREMENT_TIME, INJECTION_RATE, TOTAL_PULSE_TIME, NOMINAL_FLOW, FLOW_RANGE, VOLT_AMPERE_PROFILE, TOTAL_PULSE_TIME2, SHIFT, IS_CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/cr/injectorTests.csv');

INSERT INTO INJECTOR_TEST (ID, INJECTOR_CODE, TEST_NAME, MOTOR_SPEED, SETTED_PRESSURE, ADJUSTING_TIME,
MEASUREMENT_TIME, INJECTION_RATE, TOTAL_PULSE_TIME, NOMINAL_FLOW, FLOW_RANGE, VOLT_AMPERE_PROFILE, IS_CUSTOM)
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
  SELECT * FROM CSVREAD('classpath:/db/csv/denso/densoInjectors.csv');

--   PUMP_PARTITION

INSERT INTO MANUFACTURER_PUMP (MANUFACTURER_NAME, CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/pumps/manufacturerPump.csv');

INSERT INTO MANUFACTURER_PUMP (MANUFACTURER_NAME, CUSTOM)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/manufacturersPump_custom.csv');

INSERT INTO PUMP (PUMP_CODE, MANUFACTURER, CUSTOM, FEED_PRESSURE, ROTATION, REGULATOR_CONFIG, PRESSURE_CONTROL, REGULATOR_TYPE, SCV_CURR_INJ, SCV_MIN_CURR, SCV_MAX_CURR)
  SELECT * FROM CSVREAD('classpath:/db/csv/pumps/pump.csv');

INSERT INTO PUMP (PUMP_CODE, MANUFACTURER, CUSTOM, FEED_PRESSURE, ROTATION, REGULATOR_CONFIG, PRESSURE_CONTROL, REGULATOR_TYPE, SCV_CURR_INJ, SCV_MIN_CURR, SCV_MAX_CURR)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/pumps_custom.csv');

INSERT INTO PUMP_TEST_NAME (ID, TEST_NAME)
  SELECT * FROM CSVREAD('classpath:/db/csv/pumps/pumpTestName.csv');

INSERT INTO PUMP_TEST (ID, PUMP_CODE, TEST_NAME, VACUUM, ADJUSTING_TIME, MEASURING_TIME, MOTOR_SPEED, TARGET_PRESSURE, MIN_DIRECT_FLOW,
    MAX_DIRECT_FLOW, MIN_BACK_FLOW, MAX_BACK_FLOW, REGULATOR_CURRENT, PCV_CURRENT, CALIBRATION_MIN_I, CALIBRATION_MAX_I, CALIBRATION_I1, CALIBRATION_I2, CALIBRATION_I_OFFSET, IS_CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/pumps/pumpTest.csv');

INSERT INTO PUMP_TEST (ID, PUMP_CODE, TEST_NAME, VACUUM, ADJUSTING_TIME, MEASURING_TIME, MOTOR_SPEED, TARGET_PRESSURE, MIN_DIRECT_FLOW,
    MAX_DIRECT_FLOW, MIN_BACK_FLOW, MAX_BACK_FLOW, REGULATOR_CURRENT, PCV_CURRENT, CALIBRATION_MIN_I, CALIBRATION_MAX_I, CALIBRATION_I1, CALIBRATION_I2, CALIBRATION_I_OFFSET, IS_CUSTOM)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/pumpTests_custom.csv');

INSERT INTO PUMP_INFO (PUMP_CODE, PUMP_TYPE)
  SELECT * FROM CSVREAD('classpath:/db/csv/pumps/pumpInfo.csv');

INSERT INTO PUMP_CAR_MODEL (ID, PUMP_CODE, CAR_MODEL)
  SELECT * FROM CSVREAD('classpath:/db/csv/pumps/pumpCarModel.csv');

--   UIS_PARTITION

INSERT INTO MANUFACTURERS_UIS (MANUFACTURER_NAME, DISPLAY_ORDER, IS_CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/uis/manufacturersUIS.csv');

INSERT INTO MANUFACTURERS_UIS (MANUFACTURER_NAME, DISPLAY_ORDER, IS_CUSTOM)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/manufacturersUis_custom.csv');

INSERT INTO VOLT_AMPERE_PROFILES_UIS (PROFILE_NAME, CAM_TYPE, INLET_PRESSURE, INJECTOR_TYPE, INJECTOR_SUB_TYPE, BOOST_DISABLE, BOOST_U, BOOST_I, BATTERY_U,
    FIRST_I, FIRST_W, SECOND_I, BOOST_I2, FIRST_I2, FIRST_W2, SECOND_I2, NEGATIVE_U, BIP_PWM, BIP_WINDOW, IS_CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/uis/voltAmpereProfilesUIS.csv');

INSERT INTO VOLT_AMPERE_PROFILES_UIS (PROFILE_NAME, CAM_TYPE, INLET_PRESSURE, INJECTOR_TYPE, INJECTOR_SUB_TYPE, BOOST_DISABLE, BOOST_U, BOOST_I, BATTERY_U,
    FIRST_I, FIRST_W, SECOND_I, BOOST_I2, FIRST_I2, FIRST_W2, SECOND_I2, NEGATIVE_U, BIP_PWM, BIP_WINDOW, IS_CUSTOM)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/voltAmpereProfilesUis_custom.csv');

INSERT INTO INJECTORS_UIS (INJECTOR_CODE, MANUFACTURER_NAME, VOLT_AMPERE_PROFILE, IS_CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/uis/injectorsUIS.csv');

INSERT INTO INJECTORS_UIS (INJECTOR_CODE, MANUFACTURER_NAME, VOLT_AMPERE_PROFILE, IS_CUSTOM)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/injectorsUis_custom.csv');

INSERT INTO TEST_NAMES_UIS (ID, TEST_NAME, DISPLAY_ORDER)
  SELECT * FROM CSVREAD('classpath:/db/csv/uis/testNamesUIS.csv');

INSERT INTO INJECTOR_TESTS_UIS (ID, INJECTOR_CODE, TEST_NAME, MOTOR_SPEED, SETTED_PRESSURE, ANGLE_1, ANGLE_2, DOUBLE_COIL_OFFSET, TOTAL_PULSE_TIME_1, TOTAL_PULSE_TIME_2,
    NOMINAL_FLOW, FLOW_RANGE, ADJUSTING_TIME, MEASUREMENT_TIME, VOLT_AMPERE_PROFILE, BIP, BIP_RANGE, RACK_POSITION, IS_CUSTOM)
  SELECT * FROM CSVREAD('classpath:/db/csv/uis/injectorTestsUIS.csv');

INSERT INTO INJECTOR_TESTS_UIS (ID, INJECTOR_CODE, TEST_NAME, MOTOR_SPEED, SETTED_PRESSURE, ANGLE_1, ANGLE_2, DOUBLE_COIL_OFFSET, TOTAL_PULSE_TIME_1, TOTAL_PULSE_TIME_2,
    NOMINAL_FLOW, FLOW_RANGE, ADJUSTING_TIME, MEASUREMENT_TIME, VOLT_AMPERE_PROFILE, BIP, BIP_RANGE, RACK_POSITION, IS_CUSTOM)
  SELECT * FROM CSVREAD('~/stardex/custom_csvs/injectorTestsUis_custom.csv');

INSERT INTO REFERENCE_UIS (GENERATED_INJECTOR_CODE, INJECTOR_CODE)
  SELECT * FROM CSVREAD('classpath:/db/csv/uis/referenceUIS.csv');

-- Внимание: последовательность выполнения скриптов критична - иначе при загрузке таблиц из файлов ругается на связи FOREIGN KEY -> ID
-- сначала грузить таблицу с ID, потом таблицу с foreign_key -> ID
-- напрмер: если сначала грузить INJECTORS_UIS, а потом VOLT_AMPERE_PROFILES_UIS, то получим ошибку
-- Referential integrity constraint violation: ".....: PUBLIC.INJECTORS_UIS FOREIGN KEY(VOLT_AMPERE_PROFILE) REFERENCES PUBLIC.VOLT_AMPERE_PROFILES_UIS(PROFILE_NAME) ('VA_038130073')"