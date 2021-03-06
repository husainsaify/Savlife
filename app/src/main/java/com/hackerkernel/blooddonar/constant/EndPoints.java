package com.hackerkernel.blooddonar.constant;

/**
 * Class to hold all the
 */
public class EndPoints {
    private static final String SERVER_URL = "http://api.hackerkernel.com/savlife/";
    private static final String VERSION = "v1/";
    private static final String BASE_URL = SERVER_URL + VERSION;
    public static final String REGISTER = BASE_URL + "register.php",
            LOGIN = BASE_URL + "login.php",
            VERIFY_OTP = BASE_URL + "verifyOtp.php",
            UPDATE_USER_LOCATION = BASE_URL + "updateUserLocation.php",
            GET_BEST_DONOR = BASE_URL + "getBestDonor.php",
            SEARCH_DONOR = BASE_URL + "searchDonor.php",
            GET_DONOR_DETAIL = BASE_URL + "getDonorDetail.php",
            GET_DEALS_LIST = BASE_URL + "getDealsList.php",
            GET_DEALS = BASE_URL + "getDealsDetail.php",
            IMAGE_BASE_URL = SERVER_URL,
            POST_STATUS = BASE_URL + "postStatus.php",
            GET_DONOR_PROFILE = BASE_URL + "getUserDetail.php",
            DONATED_HISTORY = BASE_URL+ "getDonationHistory.php",
            GET_FEEDS = BASE_URL + "getFeeds.php",
            UPLOAD_PROFILE_PIC = BASE_URL + "changeUserProfilepic.php",
            ADD_DONATION_HISTORY = BASE_URL + "addDonationHistory.php",
            BOOK_DEAL = BASE_URL + "bookDeal.php",
            GET_CITY_LIST = BASE_URL + "getCity.php",
            UPDATE_GCM_TOKEN = BASE_URL + "updateGcmToken.php",
            ADD_LIKES = BASE_URL + "addLikes.php";
}
