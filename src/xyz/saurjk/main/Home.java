package xyz.saurjk.main;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xyz.saurjk.util.Constants;
import xyz.saurjk.util.StringConstants;

public class Home {

	private static final String GOEURO_API = "http://api.goeuro.com/api/v2/position/suggest/en/";

	private static String CITY_NAME = StringConstants.BLANK;
	private static final String currentDir = System.getProperty("user.dir");

	public static void main(String[] args) {
		try {
			CITY_NAME = args[0];
			if (Constants.DEBUG)
				System.out.println("Entered Location : " + CITY_NAME);

			String jsonTextFromAPI = readJSONFromAPI(CITY_NAME);
			JSONArray jsonArray = new JSONArray(jsonTextFromAPI);
			if (jsonArray.length() > 0) {
				generateCSVFile(jsonArray);
			} else {
				System.out.println("Location not found.");
			}
		} catch (IOException e) {
			if (Constants.DEBUG)
				System.out.println(e.getMessage());
		} catch (JSONException e) {
			if (Constants.DEBUG)
				System.out.println(e.getMessage());
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("No Input Provided.");
		}
	}

	private static void generateCSVFile(JSONArray jsonArray) throws JSONException, IOException {
		String csvFileName = currentDir + "\\" + CITY_NAME + ".csv";
		FileWriter fileWriter = new FileWriter(csvFileName);
		int jsonArrayLength = jsonArray.length();
		for (int ctr = 0; ctr < jsonArrayLength; ctr++) {
			JSONObject rootJSONObject = jsonArray.getJSONObject(ctr);
			String id = rootJSONObject.getString(APIConstants.TAG_ID);
			String name = rootJSONObject.getString(APIConstants.TAG_NAME);
			String type = rootJSONObject.getString(APIConstants.TAG_TYPE);

			JSONObject geoPositionJSONObject = new JSONObject(rootJSONObject.getString(APIConstants.TAG_GEO_POSITION));

			String latitude = geoPositionJSONObject.getString(APIConstants.TAG_LATITUDE);
			String longitude = geoPositionJSONObject.getString(APIConstants.TAG_LONGITUDE);
			writeToFile(fileWriter, id, name, type, latitude, longitude);
		}
		fileWriter.flush();
		fileWriter.close();
		System.out.println(jsonArrayLength + " values written to file. Saved as " + csvFileName);
	}

	private static String readJSONFromAPI(String cityName) throws IOException {
		String jsonTextFromAPI = StringConstants.BLANK;
		URL url = new URL(GOEURO_API + CITY_NAME);
		InputStream inputStream = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
		jsonTextFromAPI = readAll(reader);
		return jsonTextFromAPI;
	}

	private static void writeToFile(FileWriter writer, String id, String name, String type, String latitude,
			String longitude) throws IOException {
		writer.append(id);
		writer.append(StringConstants.COMMA);
		writer.append(name);
		writer.append(StringConstants.COMMA);
		writer.append(type);
		writer.append(StringConstants.COMMA);
		writer.append(latitude);
		writer.append(StringConstants.COMMA);
		writer.append(longitude);
		writer.append(StringConstants.NEWLINE);
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

}
