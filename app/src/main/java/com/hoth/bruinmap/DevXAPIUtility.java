package com.hoth.bruinmap;

/**
 * Created by wfehrnstrom on 2/17/18.
 */

public class DevXAPIUtility {
    public static String getCourseNum(String course) {
        String courseNum = "";
        for (int i = 0; course.charAt(i) != ' '; i++) {
            courseNum += course.charAt(i);
        }

        return courseNum;
    }

    public static String makeApiString(String subject) {
        return "http://api.ucladevx.com/courses/winter/" + subject + "/";
    }

    public static String getLocation(String input_location) {
        String location = "";
        for (int i = 0; input_location.charAt(i) != '|'; i++) {
            location += input_location.charAt(i);
        }
        return location;
    }
    /* I DONT KNOW THE RETURN TYPE FOR THIS - I WANNA RETURN LONGITUDE AND LATITUDE
    public static convertLocation
    {
        //Temp array mapping known locations to coordinates
        //Later have a legit convert function
    }
    */
}
