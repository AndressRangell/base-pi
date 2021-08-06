package com.facephi.soaplibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SoapAsync extends AsyncTask<User, SoapResult, User[]> {
	Exception _actualException;
	Soap _soap;

	private OnResult _result;
	public interface OnResult {
		public void sendResult(SoapResult result);
	}

	public void setOnResult(OnResult onCloseListener){
		_result = onCloseListener;
	}


	/**
	 * Constructor
	 * @param webServiceHost host to web service, e.g. http://host_ip:host_port/UserService.asmx
     */
	public SoapAsync(String webServiceHost) {
		try {
			_soap = new Soap();
			_soap.SetSoapAddres(webServiceHost);
		} catch (Exception ex) {
			Log.e("DEMO ERROR", ex.getMessage());
		}
	}

	private User[] FillUserList(String[] userList) {
		if(userList == null || userList.length == 0)
			return null;
		else {
			User[] result = new User[userList.length/2];
			for(int us = 0; us < result.length; us++)	{
				result[us] = new User();
				result[us].setIdentifier(userList[us*2]);
				result[us].setName(userList[(us*2) +1]);
			}

			return result;
		}
	}

	@Override
	protected void onProgressUpdate(SoapResult... values) {
		if (values == null || values[0] == null) return;

		_result.sendResult(values[0]);
		_result = null;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onPostExecute(User[] params) {
	}

	@Override
	public User[] doInBackground(User... params) {
		_actualException = null;

		if (params[0].getOperationType() != null) {
			switch (params[0].getOperationType()) {
				case Operation_Add:
					try {
						String added = _soap.Add(params[0].getName(), params[0].getTemplate());
						if (added != null && !added.isEmpty()) {
							publishProgress(new SoapResult(SoapResult.ResultType.UserAdded));
						} else {
							publishProgress(new SoapResult(SoapResult.ResultType.UserNotAdded));
						}
					}
					catch (SoapException e) {
						publishProgress(new SoapResult(SoapResult.ResultType.WebServiceError));
					} catch (Exception e) {
						publishProgress(new SoapResult(SoapResult.ResultType.WebServiceError));
					}
					break;

				case Operation_Authenticate:
					try {
						String identified = _soap.Authenticate(params[0].getIdentifier(), params[0].getTemplate());
						if (identified.equals("true")) {
							publishProgress(new SoapResult(SoapResult.ResultType.UserAuthenticated));
						} else {
							publishProgress(new SoapResult(SoapResult.ResultType.UserNotAuthenticated));
						}
					}
					catch (SoapException e) {
						publishProgress(new SoapResult(SoapResult.ResultType.WebServiceError));
					}
					catch (Exception e) {
						publishProgress(new SoapResult(SoapResult.ResultType.WebServiceError));
					}
					break;

				case Operation_GetUsers:
					try {
						String[] identified = _soap.GetList();
						SoapResult soapResult = new SoapResult(SoapResult.ResultType.UserListObtained);
						soapResult._users = FillUserList(identified); // Fill the User list
						publishProgress(soapResult);
					}
					catch (SoapException e) {
						publishProgress(new SoapResult(SoapResult.ResultType.WebServiceError));
					}
					catch (Exception e) {
						publishProgress(new SoapResult(SoapResult.ResultType.WebServiceError));
					}
					break;

				default:
					break;
			}
		}

		return null;
	}

}
