package ru.team55.gra.api.users;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.parse.*;

import java.util.ArrayList;
import java.util.List;


public class UserAccounts {



    public static List getAccountsByType(Context paramContext, String paramString) {

        Log.d("app", "Get accounts");

        ArrayList localArrayList = new ArrayList();
        AccountManager localAccountManager = AccountManager.get(paramContext);

        Account[] arrayOfAccount =  !TextUtils.isEmpty(paramString) ? localAccountManager.getAccountsByType(paramString): localAccountManager.getAccounts();

        for (int j = 0; j < arrayOfAccount.length; j++)
        {
            Account localAccount = arrayOfAccount[j];
            Log.w("app", "account name=" + localAccount.name + " type=" + localAccount.type + " string=" + localAccount.toString());
            if (!TextUtils.isEmpty(localAccount.name))
                localArrayList.add(localAccount.name);
        }

        return localArrayList;
    }


    /** =================================================================================================================
     PARSE.COM API Wrappers
     ===================================================================================================================*/


    public static void logOut(){
        ParseUser.logOut();
    }


    public static void  signUpUser(String name, String password, String mail, final UserAccountsCallback callback){

        final ParseUser user = new ParseUser();
        user.setUsername(name);
        user.setPassword(password);
        user.setEmail(mail);
        user.put("phone", ""); //set later

        try {

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        callback.onSuccess();
                    } else {
                        callback.onError(e.getCode(), e.getLocalizedMessage());
                    }
                }
            });

        }
        catch (IllegalArgumentException ex){
            callback.onError(0, ex.getLocalizedMessage());
        }



/*
        BackendlessUser userObj = new BackendlessUser();
        userObj.emailAddress = "foo@foo.com";
        userObj.password = "foo";
        userObj.name = "Bob";
        // setProperty can be used in the following scenarios:
        // 1. when "phoneNumber" is added to the user definition AND
        //    dynamic properties are turned off AND code was not regenerated
        // 2. when dynamic properties turned on the "phoneNumber" will be
        //    added when the user is registered.
        userObj.setProperty( "phoneNumber", "555-1212" );
        Backendless.UserService.register( userObj );
*/


    }


    public static void loginUser(String name, String password, final UserAccountsCallback callback){

        ParseUser.logInInBackground(name, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    callback.onSuccess();

                } else {

                    callback.onError(e.getCode(), e.getLocalizedMessage());
                }
            }
        });
    }


    public static void loginAsAnonymousUser(final UserAccountsCallback callback){

        //After logging out, an anonymous user is abandoned, and its data is no longer accessible.

        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    callback.onError(e.getCode(), e.getLocalizedMessage());

                } else {
                    callback.onSuccess();
                }
            }
        });
    }


    public static void convertFromAnonymousUser(String name, String password,String mail, final UserAccountsCallback callback){

        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(name);
        user.setPassword(password);
        user.setEmail(mail);
        user.put("phone", ""); //set later


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    callback.onSuccess();
                } else {
                    callback.onError(e.getCode(), e.getLocalizedMessage());
                }
            }
        });




        //You can convert an anonymous user into a regular user by setting the username and password,
        // then calling signUp(),
        // or by logging in or linking with a service like Facebook or Twitter.
        // The converted user will retain all of its data. To determine whether the current user is an anonymous user,
        // you can check ParseAnonymousUtils.isLinked():

    }



    public static void resetPassword(String email, final UserAccountsCallback callback){

        /*The flow for password reset is as follows:
        User requests that their password be reset by typing in their email.
        Parse sends an email to their address, with a special password reset link.
        User clicks on the reset link, and is directed to a special Parse page that will allow them type in a new password.
        User types in a new password. Their password has now been reset to a value they specify.*/

        ParseUser.requestPasswordResetInBackground(email,
                new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            callback.onSuccess();
                        } else {
                            callback.onError(e.getCode(), e.getLocalizedMessage());
                        }
                    }
                });
    }





    //По сути для первого запуска - если не залогинен войти анонимным
    //Anonymous users can also be automatically created for you without requiring a network_helpers request,
    // so that you can begin working with your user immediately when your application starts.
    public static Boolean userIsLogged(){
        ParseUser currentUser = ParseUser.getCurrentUser();
        return currentUser != null;
    }

    public static String getUserName(){
        return ParseUser.getCurrentUser().getUsername();
    }



    public static Boolean userLoggedAsAnonymous(){
        return ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser());
    }


}
