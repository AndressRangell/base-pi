package com.facephi.soaplibrary;

public enum SoapOperationType {
    Operation_Undefined,	// 0

    /**
     * Adds the user in the database.
     */
    Operation_Add, // 1

    /**
     * Authenticate the user.
     */
    Operation_Authenticate, // 1

    /**
     * Get list of users in the database.
     */
    Operation_GetUsers;	// 2



    public static int getOrdinal(SoapOperationType value) {
        switch (value) {
            case Operation_Undefined:
                return 0;
            case Operation_Add:
                return 1;
            case Operation_Authenticate:
                return 2;
            case Operation_GetUsers:
                return 3;
            default:
                return -1;
        }
    }

    public static String getMethodName(SoapOperationType value) {
        switch (value) {
            case Operation_Undefined:
                return "Undefined";
            case Operation_Add:
                return "createUser";
            case Operation_Authenticate:
                return "authenticate";
            case Operation_GetUsers:
                return "getUsers";
            default:
                return "Undefined";
        }
    }

    public static SoapOperationType getEnum(int value) {
        switch (value) {
            case 0:
                return Operation_Undefined;
            case 1:
                return Operation_Add;
            case 2:
                return Operation_Authenticate;
            case 3:
                return Operation_GetUsers;
            default:
                return Operation_Undefined;
        }
    }
}

