package app.http;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.json.JSONObject;

public class HttpOption {
	private Properties properties;
	private static final String PROP_PATH = "props.ini";
	public static final String JDK6_PATH = "JDK6_PATH";
	public static final String JDK7_PATH = "JDK7_PATH";
	public static final String JDK8_PATH = "JDK8_PATH";
	
	public static final String JDK6_JAR_PATH = "JDK6_JAR_PATH";
	public static final String JDK7_JAR_PATH = "JDK7_JAR_PATH";
	public static final String JDK8_JAR_PATH = "JDK8_JAR_PATH";
	
	private static int jdkVersion = 8; //default;
	public static final String TLSv1 = "TLSv1";
	public static final String TLSv1_1 = "TLSv1.1";
	public static final String TLSv1_2 = "TLSv1.2";
	public static final String TLSv1_3 = "TLSv1.3";
	
	public static final String URL = "URL";
	public static final String BODY = "BODY";
	public static final String METHOD = "METHOD";
	public static final String HEADER = "HEADER";
	public static final String TLS_VERSION = "TLS_VERSION";
	public static final String JDK_VERSION = "JDK_VERSION";
	public static final String JSON_PRETTY = "JSON_PRETTY";

	private static String url = "";
	

	private static String method = "GET";
	private static String body = "";
	private static String state = "";
	private static JSONObject header = null;
	private static boolean isTLSv1 = false;
	private static boolean isTLSv1_1 = false;
	private static boolean isTLSv1_2 = false;
	private static boolean isTLSv1_3 = false;
	
	private static boolean isJSONPretty = false;
		

	public HttpOption() {
		
		properties = new Properties();
		
		try {
			properties.load(new FileInputStream(PROP_PATH));
						
			this.url = properties.getProperty(URL);
			this.jdkVersion = Integer.parseInt(properties.getProperty(JDK_VERSION));
			this.method = properties.getProperty(METHOD);
			this.body = properties.getProperty(BODY);
			this.isJSONPretty = Boolean.parseBoolean(properties.getProperty(JSON_PRETTY));
			String headerJsonString = properties.getProperty(HEADER);
			String tlsVersionsString = properties.getProperty(TLS_VERSION);
			
			StringTokenizer st = new StringTokenizer(tlsVersionsString, ",");
			while(st.hasMoreTokens()) {
				String tlsVersionString = st.nextToken();
				if (tlsVersionString.equals(TLSv1)) isTLSv1 = true;
				if (tlsVersionString.equals(TLSv1_1)) isTLSv1_1 = true;
				if (tlsVersionString.equals(TLSv1_2)) isTLSv1_2 = true;
				if (tlsVersionString.equals(TLSv1_3)) isTLSv1_3 = true;
			}
			
			header = new JSONObject(headerJsonString);
			
		} catch (FileNotFoundException e) {
			properties.setProperty(JDK6_PATH, "path");
			properties.setProperty(JDK7_PATH, "path");
			properties.setProperty(JDK8_PATH, "path");
			properties.setProperty(JDK6_JAR_PATH, "path");
			properties.setProperty(JDK7_JAR_PATH, "path");
			properties.setProperty(JDK8_JAR_PATH, "path");
			properties.setProperty(JDK_VERSION, "");
			properties.setProperty(TLS_VERSION, "");
			properties.setProperty(URL, "");
			properties.setProperty(METHOD, "");
			properties.setProperty(HEADER, "");
			properties.setProperty(BODY, "");
			properties.setProperty(JSON_PRETTY, "false");

			
			try {
				properties.save( new FileOutputStream(PROP_PATH), "http tool config");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean isTLSv1() {
		return isTLSv1;
	}

	public static boolean isTLSv1_1() {
		return isTLSv1_1;
	}

	public static boolean isTLSv1_2() {
		return isTLSv1_2;
	}
	
	public static JSONObject getHeader() {
		return header;
	}

	public static void setHeader(JSONObject header) {
		HttpOption.header = header;
	}


	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public void save() throws FileNotFoundException {
		properties.save( new FileOutputStream(PROP_PATH), "http tool config");
	}
	
	public static int getJdkVersion() {
		return jdkVersion;
	}
	
	public void setJdkVersion(int jdkVersion) {
		this.jdkVersion = jdkVersion;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public static void setTLSv1(boolean isTLSv1) {
		HttpOption.isTLSv1 = isTLSv1;
	}

	public static void setTLSv1_1(boolean isTLSv1_1) {
		HttpOption.isTLSv1_1 = isTLSv1_1;
	}

	public static void setTLSv1_2(boolean isTLSv1_2) {
		HttpOption.isTLSv1_2 = isTLSv1_2;
	}

	public static boolean isTLSv1_3() {
		return isTLSv1_3;
	}

	public static void setTLSv1_3(boolean isTLSv1_3) {
		HttpOption.isTLSv1_3 = isTLSv1_3;
	}
	
	public static boolean isJSONPretty() {
		return isJSONPretty;
	}

	public static void setJSONPretty(boolean isJSONPretty) {
		HttpOption.isJSONPretty = isJSONPretty;
	}

	public static String getState() {
		synchronized (HttpOption.state) {
			return state;
		}
	}

	public static void setState(String state) {
		synchronized (HttpOption.state) {
			HttpOption.state = state;
		}
	}
}
