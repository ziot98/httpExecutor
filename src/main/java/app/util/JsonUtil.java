package app.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import javax.swing.JTable;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static boolean isJsonContent(JSONObject headerObj) {
		Iterator<String> keys = headerObj.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			String value = headerObj.getString(key);

			if (key.toLowerCase().equals("content-type")
					&& value.toLowerCase().equals("application/json")) {
				return true;
			}
		}
		return false;
	}

	
	public static JSONObject getHeaderJsonObject(JTable headerTable) {
		int rowCount = headerTable.getRowCount();
		JSONObject resultJsonObject = new JSONObject();

		for (int i = 0; i < rowCount; i++) {
			String key = (String) headerTable.getValueAt(i, 0);
			String value = (String) headerTable.getValueAt(i, 1);

			resultJsonObject.put(key, value);
		}

		return resultJsonObject;
	}

	
	public static String valiateJson(String s) {

		System.out.println("* JSON String Validation **");
		System.out.println("* JsonString : ");
		System.out.println(s);

		System.out.println("* convert JsonString : ");
		String reString = s.replaceAll("[\\t|\\n|\\r]", "");
		System.out.println(reString);

		try {
			JSONObject j = new JSONObject(s);
			System.out.println("Validation Success");

		} catch (JSONException e) {
			System.out.println("Validation Failed");
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));

			System.out.println(errors.toString());
		}

		return reString;
	}

	
	public static String getPrettyJson(String jsonString) {
		try {
			String valiatedJsonString = valiateJson(jsonString);
			Object jsonObj = objectMapper.readValue(valiatedJsonString,
					Object.class);
			String prettyJson = objectMapper.writerWithDefaultPrettyPrinter()
					.writeValueAsString(jsonObj);
			return prettyJson;
		} catch (JsonProcessingException e) {
			// not a json
			return jsonString;
		}
	}

}
