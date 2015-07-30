package it.jrc.smart.fire;

import it.jrc.smart.fire.model.ActiveFire;
import it.jrc.smart.fire.model.BurnedArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vividsolutions.jts.geom.Envelope;

public class FireDAO {

	private static final String UTF_8 = "UTF-8";

	private static String readJsonFromUrl(String url) {
		InputStream is;
		try {
			is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName(UTF_8)));
			String jsonText = readAll(rd);
			return jsonText;
		} catch (MalformedURLException e) {

		} catch (IOException e) {

		}
		return null;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static void main(String[] args) {

		// Session session = TestHibernateSessionManager.openSession();

		// getDatesBetweenTheseDates(fromDate, toDate);

		// getFiresByDate(-123.65205256969075,48.31421421030944,-123.24327664326452,
		// 48.75880075305904, new Date(), new Date());
		// String x =
		// getFireCountByDate(-123.65205256969075,48.31421421030944,-123.24327664326452,
		// 48.75880075305904, new Date(), new Date());

		double minx = 10.9615703155282533;
		double miny = 11.802171870755888;
		double maxx = 11.8940815542246203;
		double maxy = 12.672085534005626;

		Envelope env = new Envelope(minx, miny, maxx, maxy);
		
		List<ActiveFire> af = getFires(env, new Date(0));
		for (ActiveFire activeFire : af) {
			System.out.println(activeFire.getStamp());
			System.out.println(activeFire.getX());
			System.out.println(activeFire.getY());
		}

//		Collection<BurnedArea> ba = getBurnedAreas(env, new Date(0));
//
//		for (BurnedArea activeFire : ba) {
//			System.out.println(activeFire.getStamp());
//		}

	}

	public static List<BurnedArea> getBurnedAreas(Envelope env, Date mostRecent) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String url = String
				.format("http://dopa-services.jrc.ec.europa.eu/services/fire/estation/get_burned_area_points?minx=%s&miny=%s&maxx=%s&maxy=%s&srid=%s&from_time=%s",
						env.getMinX(), env.getMinY(), env.getMaxX(),
						env.getMaxY(), 4326,
						df.format(mostRecent).replace(" ", "%20"));

//		System.out.println(url);

		String readJson = readJsonFromUrl(url);

		JsonParser parser = new JsonParser();
		JsonObject obj = (JsonObject) parser.parse(readJson);
		JsonArray arr = obj.get("records").getAsJsonArray();

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.create();

		Type collectionType = new TypeToken<List<BurnedArea>>() {
		}.getType();

		List<BurnedArea> x = gson.fromJson(arr, collectionType);

		System.err.println(x.size() + " bas retrieved from url: " + url);

		return x;
	}

	public static List<ActiveFire> getFires(Envelope env, Date mostRecent) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
//		http://dopa-services.jrc.ec.europa.eu/services/fire/estation/

		String url = String
				.format("http://dopa-services.jrc.ec.europa.eu/services/fire/estation/get_recent_fires?minx=%s&miny=%s&maxx=%s&maxy=%s&srid=%s&from_time=%s",
						env.getMinX(), env.getMinY(), env.getMaxX(),
						env.getMaxY(), 4326,
						df.format(mostRecent).replace(" ", "%20"));

//		System.out.println(url);

		String readJson = readJsonFromUrl(url);

		JsonParser parser = new JsonParser();
		JsonObject obj = (JsonObject) parser.parse(readJson);
		JsonArray arr = obj.get("records").getAsJsonArray();

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.create();

		Type collectionType = new TypeToken<List<ActiveFire>>() {
		}.getType();

		List<ActiveFire> x = gson.fromJson(arr, collectionType);

		System.err.println(x.size() + " fires retrieved from url: " + url);

		return x;
	}
}
