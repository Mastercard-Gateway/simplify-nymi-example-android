package com.simplify.android.sdk;


import com.simplify.android.sdk.model.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class Utils
{
    /**
     * Extracts the content of an input stream into a string
     * @param is        The input stream
     * @return          The string content of the input stream
     * @throws IOException
     */
    public static String inputStreamToString(InputStream is) throws IOException
    {
        // get buffered reader from stream
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // read stream into string builder
        String line = "";
        while ((line = rd.readLine()) != null) {
            total.append(line);
        }

        return total.toString();
    }

    /**
     * Validates that a card number passes Luhn and matches minimum length
     * requirements for a specific card type
     * @param number    The card number
     * @param type      The card type to validate against
     * @return          True or False
     */
    public static boolean validateCardNumber(String number, Card.Type type)
    {
        if (number == null || type == null) {
            return false;
        }

        // numbers only, please
        number = number.replaceAll("[^\\d]+", "");

        // match against type prefix
        if (!type.prefixMatches(number)) {
            return false;
        }

        // ensure minumum length is satisfied
        int length = number.length();
        if (length == 0 || length < type.getMinLength()) {
            return false;
        }

        int sum = 0;
        for (int i = length - 2; i >= 0; i -= 2) {
            char c = number.charAt(i);
            if (c < '0' || c > '9') return false;

            // Multiply digit by 2.
            int v = (c - '0') << 1;

            // Add each digit independently.
            sum += v > 9 ? 1 + v - 10 : v;
        }

        // Add the rest of the non-doubled digits
        for (int i = length - 1; i >= 0; i -= 2) {
            sum += number.charAt(i) - '0';
        }

        // Double check that the Luhn check-digit at the end brings us to a neat multiple of 10
        return sum % 10 == 0;
    }

    /**
     * Validates that a provided expiration date is in the future
     * @param month     The expiration date, format: MM/YY
     * @param year      The expiration date, format: MM/YY
     * @return          True or False
     */
    public static boolean validateCardExpiration(String month, String year)
    {
        if (month.trim().length() == 0 || year.trim().length() == 0) {
            return false;
        }

        return validateCardExpiration(Integer.parseInt(month), Integer.parseInt(year));
    }

    /**
     * Validates that a provided expiration date is in the future
     * @param month     The month number, 1 - 12
     * @param year      The 2 or 4 digit year number
     * @return          True or False
     */
    public static boolean validateCardExpiration(int month, int year)
    {
        if (year < 100) {
            year += 2000;
        }

        if (month == 0) {
            return false;
        }

        Calendar expire = Calendar.getInstance();
        expire.set(Calendar.MONTH, month - 1);
        expire.set(Calendar.YEAR, year);
        expire.roll(Calendar.MONTH, 1);

        Calendar now = Calendar.getInstance();
        return now.before(expire);
    }

    /**
     * Validates that a cvc code matches the length required by the card type
     * @param cvc       The cvc code
     * @param type      The card type
     * @return          True or False
     */
    public static boolean validateCardCvc(String cvc, Card.Type type)
    {
        return (cvc != null && cvc.trim().length() == type.getCvcLength());
    }
}
