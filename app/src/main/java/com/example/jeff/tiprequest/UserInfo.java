package com.example.jeff.tiprequest;

class UserInfo {

    private static String accountName;
    private static String accountID = "No User ID";

    public UserInfo(){
    }

    public static String getAccountID() {
        return accountID;
    }
    public static void setAccountID(String accountID) {
        UserInfo.accountID = accountID;
    }

    public static String getAccountName() {
        return accountName;
    }
    public static void setAccountName(String accountName) {
        UserInfo.accountName = accountName;
    }
}
