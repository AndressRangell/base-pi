package com.facephi.soaplibrary;

public class SoapException extends Exception {

    private static final long serialVersionUID = 1L;
    private SoapExceptionType typeException = SoapExceptionType.UnhandledSoapException;
    private String paramError = "";

    public SoapException(String message) {
        super(message);
    }

    public SoapException(String message, SoapExceptionType code) {
        super(message);
        typeException = code;
    }

    public SoapException(String message, Throwable throwable, SoapExceptionType code) {
        super(message, throwable);
        typeException = code;
    }

    public SoapException(String message, SoapExceptionType code, String paramErrorException) {
        super(message);
        typeException = code;
        paramError = paramErrorException;
    }

    public SoapException(String message, Throwable throwable, SoapExceptionType code, String paramErrorException) {
        super(message, throwable);
        typeException = code;
        paramError = paramErrorException;
    }

    public SoapExceptionType getTypeException() {
        return typeException;
    }

    public String getParamError() {
        return paramError;
    }


    public enum SoapExceptionType {
        ArgumentNullException,
        ArgumentOutOfRangeException,
        ArgumentException,
        UnhandledSoapException;

        public static int getOrdinal(SoapExceptionType value) {
            switch (value) {
                case ArgumentNullException:
                    return -1;
                case ArgumentOutOfRangeException:
                    return -2;
                case ArgumentException:
                    return -3;
                default:
                    return 0;
            }
        }

        public static String getMethodName(SoapExceptionType value) {
            switch (value) {
                case ArgumentNullException:
                    return "ArgumentNullException";
                case ArgumentOutOfRangeException:
                    return "ArgumentOutOfRangeException";
                case ArgumentException:
                    return "ArgumentException";
                default:
                    return "UnhandledSoapException";
            }
        }

        public static SoapExceptionType getEnum(int value) {
            switch (value) {
                case -1:
                    return ArgumentNullException;
                case -2:
                    return ArgumentOutOfRangeException;
                case -3:
                    return ArgumentException;
                default:
                    return UnhandledSoapException;
            }
        }
    }
}
