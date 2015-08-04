package nu.pto.androidapp.base.util;

/**
 * Created by zaven on 4/17/14.
 */
public class ValidatorUtil {
    public static boolean isValidEmailAddress(String email) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(".+@.+\\.[a-z]+");
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

//    public static boolean isValidGender(String gender) {
//        if (gender.toLowerCase().equals("man") || gender.toLowerCase().equals("kvinna")) {
//            return true;
//        }
//        return false;
//    }

    public static boolean isValidDateOfBirth(String dob) {
        try {
            int intDOB = Integer.parseInt(dob);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidPhoneNumber(String phone) {

        java.util.regex.Pattern p = java.util.regex.Pattern.compile("[0-9 ]+");
        java.util.regex.Matcher m = p.matcher(phone);
        return m.matches();
    }
}
