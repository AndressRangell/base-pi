package com.facephi.soaplibrary;

public class SoapResult {
	public enum ResultType {
		UserAdded,
		UserExists,
		UserNotFound,
		UserNotAdded,
		UserIdentified,
		UserNotIdentified,
		UserAuthenticated,
		UserNotAuthenticated,
		EmptyDatabase,
		UserListObtained,
		WebServiceError
	}

	public String _name;
	public ResultType _resultType;
	public User[] _users;

	public SoapResult(ResultType resultType) {
		_resultType = resultType;
	}

	public SoapResult(ResultType resultType, String name) {
		_resultType = resultType;
		_name = name;
	}
}
