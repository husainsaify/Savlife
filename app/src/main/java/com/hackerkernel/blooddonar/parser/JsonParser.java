    package com.hackerkernel.blooddonar.parser;

    import com.hackerkernel.blooddonar.constant.Constants;
    import com.hackerkernel.blooddonar.pojo.DealsListPojo;
    import com.hackerkernel.blooddonar.pojo.DealsPojo;
    import com.hackerkernel.blooddonar.pojo.DonorPojo;
    import com.hackerkernel.blooddonar.pojo.DonorListPojo;
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

        public static List<DonorListPojo> DonorParser(JSONArray dataArray) throws JSONException {
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

        public static List<DealsListPojo> ParseDealsList(JSONArray data) throws JSONException {
           List<DealsListPojo> list = new ArrayList<>();
            for (int i = 0; i <data.length() ; i++) {
                JSONObject obj = data.getJSONObject(i);
                DealsListPojo pojo = new DealsListPojo();
                pojo.setDeal(obj.getString(Constants.COM_DEAL));
                pojo.setHospitalName(obj.getString(Constants.COM_LABNAME));
                pojo.setDescription(obj.getString(Constants.COM_DESCRIPTION));
                pojo.setDealsId(obj.getString(Constants.COM_ID));
                list.add(pojo);
            }
            return list;
        }

        public static DealsPojo ParseDetailDeals(JSONArray data) throws JSONException {
            DealsPojo pojo = new DealsPojo();
            for (int i = 0; i <data.length() ; i++) {
                JSONObject obj = data.getJSONObject(i);
                pojo.setLabName(obj.getString(Constants.COM_LABNAME));
                pojo.setDescription(obj.getString(Constants.COM_DESCRIPTION));
                pojo.setOff(obj.getString(Constants.COM_DEAL));
                pojo.setOrignal_prize(obj.getString(Constants.COM_ORIGNAL_PRIZE));
                pojo.setSpecial_prize(obj.getString(Constants.COM_SPECIAL_PRIZE));
                //pojo.setTimming(obj.getString(Constants.COM_Timing));

            }
            return pojo;

        }

    }
