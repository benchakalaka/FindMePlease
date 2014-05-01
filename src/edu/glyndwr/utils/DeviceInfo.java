package edu.glyndwr.utils;



import java.io.Serializable;

public class DeviceInfo implements Serializable {

	/**
	 * default id
	 */
	private static final long serialVersionUID = 1L;

	public String password;

	/**
	 * location data in string format
	 */
	public String locationData;

	/**
	 * separate network info
	 */
	public String callState;
	public String cellLocationString;
	public String deviceId;
	public String deviceSoftwareVersion;
	public String phoneNumber;
	public String networkCountryIso;
	public String networkOperator;
	public String networkOperatorName;
	public String networkRoaming;
	public String networkType;
	public String phoneTypeString;
	public String simCountryIso;
	public String simOperator;
	public String simOperatorName;
	public String simSerialNumber;
	public String simSubscriberId;
	public String simStateString;

	/**
	 * location info
	 */
	public static double longitude;
	public static double latitude;
	public static double altitude;
	public static float accuracy;
	public String bestProvider;


	public DeviceInfo() {
		init();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\nPHONE INFO");
		sb.append("  \nCALL STATE = ").append(callState);
		sb.append("  \nCELL LOCATION STRING = ").append(cellLocationString);
		sb.append("  \nDEVICE ID = ").append(deviceId);
		sb.append("  \nDEVICE SOFTWARE VERSION = ").append(
				deviceSoftwareVersion);
		sb.append(" \nUSER PASSWORD = ").append(password);
		sb.append("  \nPHONE NUMBER = ").append(phoneNumber);
		sb.append("  \nNETWORK COUNTRY ISO = ").append(networkCountryIso);
		sb.append("  \nNETWORK OPERATOR = ").append(networkOperator);
		sb.append("  \nNETWORK OPERATOR NAME = ").append(networkOperatorName);
		sb.append("  \nPHONE TYPE STRING = ").append(phoneTypeString);
		sb.append("  \nSIM COUNTRY ISO = ").append(simCountryIso);
		sb.append("  \nSIM OPERATOR = ").append(simOperator);
		sb.append("  \nSIM OPERATOR NAME = ").append(simOperatorName);
		sb.append("  \nSIM SERIAL NUMBER = ").append(simSerialNumber);
		sb.append("  \nSIM SUBSRIBER ID = ").append(simSubscriberId);
		sb.append("  \nSIM STATE STRING = ").append(simStateString);

		sb.append("\n\nLOCATION INFO");
		sb.append("  \nBEST PROVIDER = ").append(bestProvider);
		sb.append("  \nLONGTITUDE = ").append(longitude);
		sb.append("  \nLATITUDE  = ").append(latitude);
		sb.append("  \nACCURACY = ").append(accuracy);
		sb.append("  \nALTITUDE = ").append(altitude);
		return sb.toString();

	}

	private void init() {
		callState = Constants.UNKNOWN_STRING;
		cellLocationString = Constants.UNKNOWN_STRING;
		deviceId = Constants.UNKNOWN_STRING;
		deviceSoftwareVersion = Constants.UNKNOWN_STRING;
		phoneNumber = Constants.UNKNOWN_STRING;
		networkCountryIso = Constants.UNKNOWN_STRING;
		networkOperator = Constants.UNKNOWN_STRING;
		networkOperatorName = Constants.UNKNOWN_STRING;
		phoneTypeString = Constants.UNKNOWN_STRING;
		simCountryIso = Constants.UNKNOWN_STRING;
		simOperator = Constants.UNKNOWN_STRING;
		simOperatorName = Constants.UNKNOWN_STRING;
		simSerialNumber = Constants.UNKNOWN_STRING;
		simSubscriberId = Constants.UNKNOWN_STRING;
		simStateString = Constants.UNKNOWN_STRING;
		bestProvider = Constants.UNKNOWN_STRING;
		locationData = Constants.UNKNOWN_STRING;
		password = Constants.UNKNOWN_STRING;

		longitude = Constants.EMPTY_INT_VALUE;
		latitude = Constants.EMPTY_INT_VALUE;
		altitude = Constants.EMPTY_INT_VALUE;
		accuracy = Constants.EMPTY_FLOAT_VALUE;
	}
}
