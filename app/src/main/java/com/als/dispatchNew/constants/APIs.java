package com.als.dispatchNew.constants;

public class APIs {



    /*=============== ALS SERVER URL =============== */
    static String DOMAIN_LOCAL_SIMULATOR 					    = "http://192.168.0.10:8267/api/DispatchApi/";
    static String DOMAIN_LOCAL_MOBILE 							= "http://182.73.78.171:8345/api/DispatchApi/";
    static String DOMAIN_PRODUCTION 							= "http://alsdispatchapi.alsrealtime.com/api/DispatchApi/";


    /*========================= API URLs =====================*/
    public static String DOMAIN_URL 						= DOMAIN_PRODUCTION ;     	// DISPATCH DOMAIN

    public static String LOGIN_USER 						= DOMAIN_URL + "Login/";
    public static String GET_DRIVER_JOB 					= DOMAIN_URL + "GetDriverJob/";
    public static String GET_LOAD_DETAIL 					= DOMAIN_URL + "GetLoadDetail/";
    public static String LOAD_CANCELLED_PARAMETERS 			= DOMAIN_URL + "LoadCancelled/";
    public static String LOAD_PICKED_UP 					= DOMAIN_URL + "LoadPickedup/";
    public static String LOAD_DELIVERED 					= DOMAIN_URL + "LoadDelivered/";
    public static String TRIP_HISTORY 						= DOMAIN_URL + "GetDriverDeliveredTrip/";
    public static String TRIP 								= DOMAIN_URL + "GetDriverTrip/";   // (DriverId, deviceID)
    public static String CONFIRM_DELIVERY 					= DOMAIN_URL + "UpdateLoadDelivered/";  //
    public static String PHOTO_UPLOAD 						= DOMAIN_URL + "UploadDeliveryDocument/";
    public static String LOCAL_DOC_UPLOAD    				= DOMAIN_URL + "UploadLocalDeliveryDocument/";
    public static String GET_JOB_LOAD_TYPE 					= DOMAIN_URL + "GetJobLoadTypes/";
    public static String GET_TRIP_ROUTE 					= DOMAIN_URL + "GetTripRoute/";
    public static String VEHICLE_TRACKING 					= DOMAIN_URL + "VehicleTracking";
    public static String CONFIRMED_PICK_DROP_LOAD 			= DOMAIN_URL + "ConfirmedPickAndDropLoad";
    public static String GET_CURRENY_EXPENSE 				= DOMAIN_URL + "GetCurrenyAndDriverExpenseReasonTypes";
    public static String UPDATE_DRIVER_TRIP_EXPENSES_DETAIL = DOMAIN_URL + "UpdateDriverTripExpensesDetail";
    public static String GET_DRIVER_TRIP_EXPENSES_DETAIL 	= DOMAIN_URL + "getDriverTripExpensesDetail";
    public static String MAKE_LOAD_ARRIVED 					= DOMAIN_URL + "MakeLoadArrived";
    public static String GET_TRIP_DETAILS 					= DOMAIN_URL + "GetDriverTripDetailBySearchDate";
    public static String UPLOAD_DRIVER_IMAGE 				= DOMAIN_URL + "UploadDriverImage";
    public static String LOCAL_LOAD_DELIVERED 				= DOMAIN_URL + "LocalLoadDelivered";
    public static String DELETE_TRIP_EXPENSE 				= DOMAIN_URL + "DeleteTripExpense";
    public static String UPDATE_LOAD_STATUS 				= DOMAIN_URL + "UpdateLoadStatus";


    // Only for this API (GetOrginDistance) domain is changed
    static String INDIAN_SIMULATOR = "http://192.168.0.10:8286/api/LogisticsApi/";
    static String INDIAN_MOBILE    = "http://182.73.78.171:8286/api/LogisticsApi/";
    static String DEV              = "http://dev.alsrealtime.com/api/LogisticsApi/";
    static String PRODUCTION       = "https://alsrealtime.com/api/LogisticsApi/";

    public static String GET_ORGIN_DISTANCE 				= DEV + "GetOrginDistance";
    public static String GET_LOAD_AND_GPS    				= DEV + "GetLoadGPS";

}
