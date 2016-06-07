    package com.hackerkernel.blooddonar.parser;

    import com.hackerkernel.blooddonar.constant.Constants;
    import com.hackerkernel.blooddonar.pojo.DealsListPojo;
    import com.hackerkernel.blooddonar.pojo.DealsPojo;
    import com.hackerkernel.blooddonar.pojo.DonorListPojo;
    import com.hackerkernel.blooddonar.pojo.DonorPojo;
    import com.hackerkernel.blooddonar.pojo.FeedsListPojo;
    import com.hackerkernel.blooddonar.pojo.ProfileDetailPojo;
    import com.hackerkernel.blooddonar.pojo.SimplePojo;
    import com.hackerkernel.blooddonar.storage.MySharedPreferences;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.List;

    /**
     * Class to parse json response
     */
    public class JsonParser {
        private static final String TAG = JsonParser.class.getSimpleName();

        public static SimplePojo SimpleParser(String response) throws JSONException {
            JSONObject jo = new JSONObject(response);
            SimplePojo simplePojo = new SimplePojo();
            simplePojo.setMessage(jo.getString(Constants.COM_MESSAGE));
            simplePojo.setReturned(jo.getBoolean(Constants.COM_RETURN));
            return simplePojo;
        }

        /*
        * Method to parse user details from verify otp response
        * and store them in SP
        *
        * _____
        * this method will not validate
        * it will just parse user data and store them in sp
        * */
        public static void VerifyOtpParser(String response, MySharedPreferences sp) throws JSONException {
            JSONObject jo = new JSONObject(response);
            JSONArray data = jo.getJSONArray(Constants.COM_DATA);
            for (int i = 0; i < data.length(); i++) {
                JSONObject o = data.getJSONObject(i);
                String id = o.getString(Constants.COM_ID);
                String fullname = o.getString(Constants.COM_FULLNAME);
                String mobile = o.getString(Constants.COM_MOBILE);
                String age = o.getString(Constants.COM_AGE);
                String blood = o.getString(Constants.COM_BLOOD);
                String createdAt = o.getString(Constants.COM_CREATED_AT);

                //store in sp
                sp.setUserFullname(fullname);
                sp.setUserMobile(mobile);
                sp.setUserAge(age);
                sp.setUserBloodGroup(blood);
                sp.setUserId(id);
                sp.setUserCreatedAt(createdAt);
            }
        }

        public static List<DonorListPojo> DonorListParser(JSONArray dataArray) throws JSONException {
            List<DonorListPojo> list = new ArrayList<>();
            for (int i = 0; i <dataArray.length() ; i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                DonorListPojo pojo = new DonorListPojo();
                pojo.setUserId(obj.getString(Constants.COM_ID));
                pojo.setUserName(obj.getString(Constants.COM_FULLNAME));
                pojo.setImageUrl(obj.getString(Constants.COM_IMG));
                pojo.setUserBloodGroup(obj.getString(Constants.COM_BLOOD));
                list.add(pojo);
            }
            return list;
        }

        public static DonorPojo DetailDonorParser(JSONArray dataArray) throws JSONException {
            DonorPojo pojo = new DonorPojo();
            for (int i = 0; i <dataArray.length() ; i++) {
                JSONObject obj = dataArray.getJSONObject(i);
                pojo.setFullName(obj.getString(Constants.COM_FULLNAME));
                pojo.setCity(obj.getString(Constants.LOC_CITY));
                pojo.setAge(obj.getString(Constants.COM_AGE));
                pojo.setBloodGroup(obj.getString(Constants.COM_BLOOD));
                pojo.setGender(obj.getString(Constants.COM_GENDER));
                pojo.setId(obj.getString(Constants.COM_ID));
                pojo.setImageUrl(obj.getString(Constants.COM_IMG));
            }
            return pojo;
        }

        public static List<DealsListPojo> DealsListParser(JSONArray data) throws JSONException {
           List<DealsListPojo> list = new ArrayList<>();
            for (int i = 0; i <data.length() ; i++) {
                JSONObject obj = data.getJSONObject(i);
                DealsListPojo pojo = new DealsListPojo();
                pojo.setDeal(obj.getString(Constants.COM_DEAL));
                pojo.setHospitalName(obj.getString(Constants.COM_LABNAME));
                pojo.setDescription(obj.getString(Constants.COM_DESCRIPTION));
                pojo.setDealsId(obj.getString(Constants.COM_ID));
                pojo.setImageUrl(obj.getString(Constants.COM_IMG));
                list.add(pojo);
            }
            return list;
        }

        public static DealsPojo DetailDealsParser(JSONArray data) throws JSONException {
            DealsPojo pojo = new DealsPojo();
            for (int i = 0; i <data.length() ; i++) {
                JSONObject obj = data.getJSONObject(i);
                pojo.setLabName(obj.getString(Constants.COM_LABNAME));
                pojo.setDescription(obj.getString(Constants.COM_DESCRIPTION));
                pojo.setOff(obj.getString(Constants.COM_DEAL));
                pojo.setOrignal_prize(obj.getString(Constants.COM_ORIGNAL_PRIZE));
                pojo.setSpecial_prize(obj.getString(Constants.COM_SPECIAL_PRIZE));
                pojo.setImageUrl(obj.getString(Constants.COM_IMG));
                pojo.setCode(obj.getString(Constants.COM_CODE));

            }
            return pojo;

        }
        public static List<String> DonationHistoryParser(JSONArray data) throws JSONException {
            List<String> mList = new ArrayList<>();
            for (int i = 0; i <data.length() ; i++) {
                JSONObject obj = data.getJSONObject(i);
                String lastDonated = obj.getString(Constants.COM_DATE);
                mList.add(lastDonated);
            }
            return mList;
        }

        /*
        * Method to parse feeds response
        * PS:
        * This method does not validate stuff it just parse
        * */
        public static List<FeedsListPojo> FeedsListParser(JSONArray dataArray) throws JSONException {
            List<FeedsListPojo> list = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject jo = dataArray.getJSONObject(i);
                FeedsListPojo c = new FeedsListPojo();
                c.setStatus(jo.getString(Constants.COM_STATUS));
                c.setImage(jo.getString(Constants.COM_IMG));
                c.setType(jo.getString(Constants.FEED_TYPE));
                c.setTimestamp(jo.getString(Constants.COM_TIME));
                c.setUserId(jo.getString(Constants.FEED_USER_ID));
                c.setUserMobile(jo.getString(Constants.FEED_USER_MOBILE));
                c.setUserFullname(jo.getString(Constants.FEED_USER_FULLNAME));
                c.setUserImage(jo.getString(Constants.FEED_USER_IMAGE));
                //add to list
                list.add(c);
            }
            return list;
        }
        public static ProfileDetailPojo ParseUserProfile(JSONArray data) throws JSONException {
            ProfileDetailPojo pojo = new ProfileDetailPojo();
            for (int i = 0; i <data.length() ; i++) {
                JSONObject obj = data.getJSONObject(i);
                pojo.setProfileNAme(obj.getString(Constants.COM_FULLNAME));
                pojo.setProfileBlood(obj.getString(Constants.COM_BLOOD));
                pojo.setProfileAge(obj.getString(Constants.COM_AGE));
                pojo.setProfileCity(obj.getString(Constants.LOC_CITY));
                pojo.setProfileGender(obj.getString(Constants.COM_GENDER));
                pojo.setProfileId(obj.getString(Constants.COM_ID));
                pojo.setProfileNum(obj.getString(Constants.COM_MOBILE));
                pojo.setProfileURL(obj.getString(Constants.COM_IMG));

            }
            return pojo;
        }

        /*
        * Method to parse city list response
        * PS: this method will not validate any stuff this will just
        * parse all the city and return List<String>
        * */
        public static List<String> CityListParser(JSONArray data) throws JSONException {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                String city = data.getString(i);
                list.add(city);
            }
            return list;
        }
    }
