package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.DataValidationException;
import com.univocity.parsers.conversions.ValidatedConversion;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import java.util.Set;
import java.util.regex.Matcher;

public class Github_363 {

      @Test()
      public void testNullInput() {
          //Input is null and nullable = true , noneof.contains(null) = true
          String regexToMatch = null;
          boolean nullable = true;
          boolean allowBlanks = true;
          String[] oneOf = {};
          String[] noneOf = {null};
          try{
              ValidatedConversion valCon1 = new ValidatedConversion(nullable, allowBlanks, oneOf, noneOf, regexToMatch);
              valCon1.execute(null);
          }catch(DataValidationException e){
                assertTrue(e.getMessage().contains("Value 'null' is not allowed."));
          }
          //Input is null and nullable is true , noneof == null is true
          ValidatedConversion valCon2 = new ValidatedConversion(nullable, allowBlanks, oneOf, null, regexToMatch);
          try{
              valCon2.execute(null);
          }catch(Exception e){
            assertFalse(e == null);
          }

          // input is null and nullable is false, oneOf has null
          nullable = false;
          String[] oneOf3 = {null};
          String[] noneOf3 = {};
          ValidatedConversion valCon3 = new ValidatedConversion(nullable, allowBlanks, oneOf3, noneOf3, regexToMatch);
          try{
              valCon3.execute(null);
          }catch(DataValidationException e){
              assertFalse(e == null);
          }
      }

        @Test()
        public void testNotNullInput() {
          // noneof has the input string
            String regexToMatch = null;
            boolean nullable = true;
            boolean allowBlanks = true;
            String[] oneOf = {};
            String[] noneOf = {"ab"};
            try{
                ValidatedConversion valCon1 = new ValidatedConversion(nullable, allowBlanks, oneOf, noneOf, regexToMatch);
                valCon1.execute("ab");
            }catch(DataValidationException e){
                assertTrue(e.getMessage().contains("Value 'ab' is not allowed."));
            }

         // matcher is not null and input matches, noneOf is null
            regexToMatch = "ab";
            try{
                ValidatedConversion valCon2 = new ValidatedConversion(nullable, allowBlanks, oneOf, null, regexToMatch);
                valCon2.execute("ab");
            }catch(DataValidationException e){
                assertFalse(e == null);
            }

         // input is blank and allowblanks is true and noneof contains input
            String[] noneOf3 = {"  "};
            regexToMatch = null;
            try{
                ValidatedConversion valCon3 = new ValidatedConversion(nullable, allowBlanks, oneOf, noneOf3, regexToMatch);
                valCon3.execute("  ");
            }catch(DataValidationException e){
                assertTrue(e.getMessage().contains("Value '  ' is not allowed."));
            }

            // input is blank and allowblanks is true and noneof contains input
            String[] noneOf4 = {"abc"};
            try{
                ValidatedConversion valCon4 = new ValidatedConversion(nullable, allowBlanks, oneOf, noneOf4, regexToMatch);
                valCon4.execute("  ");
            }catch(DataValidationException e){
                assertFalse(e == null);
            }

        }


}
