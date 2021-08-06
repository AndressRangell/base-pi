package com.facephi.soaplibrary;

public enum IconType {
    OK("Ok"),
    INFO("Info"),
    ERROR("Error");

    private String stringValue;

    private IconType(String toString) {
        stringValue = toString;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public static int getOrdinal(IconType value) {
        switch (value) {
            case OK:
                return 0;
            case INFO:
                return 1;
            case ERROR:
                return 2;
            default:
                return 2;
        }
    }

    public static String getMethodName(IconType value) {
        switch (value) {
            case OK:
                return "Ok";
            case INFO:
                return "Info";
            case ERROR:
                return "Error";
            default:
                return "Undefined";
        }
    }

    public static IconType getEnum(int value) {
        switch (value) {
            case 0:
                return OK;
            case 1:
                return INFO;
            case 2:
                return ERROR;
            default:
                return ERROR;
        }
    }
}
