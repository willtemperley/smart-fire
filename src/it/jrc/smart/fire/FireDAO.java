package it.jrc.smart.fire;

import it.jrc.smart.fire.model.ActiveFire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

	public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//	public static Collection<ActiveFire> getFiresByDate(double minx,
//			double miny, double maxx, double maxy, Date fromDate, Date toDate) {
//		String url;
//
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//
//		url = String
//				.format("http://dopa-services.jrc.ec.europa.eu/services/estation/get_fires_by_date?minx=%s&miny=%s&maxx=%s&maxy=%s&fromdate=%s&todate=%s&srid=%s",
//						minx, miny, maxx, maxy, df.format(fromDate),
//						df.format(toDate), 4326);
//
//		String readJson = readJsonFromUrl(url);
//
//		JsonParser parser = new JsonParser();
//		JsonObject obj = (JsonObject) parser.parse(readJson);
//		JsonArray arr = obj.get("records").getAsJsonArray();
//
//		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//				.create();
//
//		Type collectionType = new TypeToken<Collection<ActiveFire>>() {
//		}.getType();
//
//		Collection<ActiveFire> x = gson.fromJson(arr, collectionType);
//
//		System.out.println("Fires retrieved from url: " + url);
//
//		return x;
//	}

//	public static String getFireCountByDate(double minx, double miny,
//			double maxx, double maxy, Date fromDate, Date toDate) {
//		String url;
//
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//
//		url = String
//				.format("http://dopa-services.jrc.ec.europa.eu/services/estation/get_fire_count_by_date?minx=%s&miny=%s&maxx=%s&maxy=%s&fromdate=%s&todate=%s&srid=%s",
//						minx, miny, maxx, maxy, df.format(fromDate),
//						df.format(toDate), 4326);
//
//		String readJson = readJsonFromUrl(url);
//
//		JsonParser parser = new JsonParser();
//		JsonObject obj = (JsonObject) parser.parse(readJson);
//		JsonArray arr = obj.get("records").getAsJsonArray();
//
//		JsonObject x = (JsonObject) arr.get(0);
//		JsonObject y = (JsonObject) arr.get(1);
//		String fireCount = x.get("count").getAsString();
//		String dateCount = y.get("count").getAsString();
//
//		return fireCount + " fires over " + dateCount + " days between "
//				+ df.format(fromDate) + " and " + df.format(toDate);
//
//	}

	public static List<Date> getDatesBetweenTheseDates(Date fromDate,
			Date toDate) {
		List<Date> dates = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();

		cal.setTime(fromDate);
		while (cal.getTime().before(toDate)) {
			cal.add(Calendar.DATE, 1);
			dates.add(cal.getTime());
		}
		return dates;
	}

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

		double minx = 1.9615703155282533;
		double miny = 11.802171870755888;
		double maxx = 2.8940815542246203;
		double maxy = 12.672085534005626;

		Envelope env = new Envelope(minx, miny, maxx, maxy);

		Collection<ActiveFire> x;

		try {

			x = getRecentFires(env, df.parse("2014-01-01 00:00:00"));

			for (ActiveFire activeFire : x) {
				System.out.println(activeFire.getFrp());
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static List<ActiveFire> getRecentFires(Envelope env, Date mostRecent) {


		String url = String
				.format("http://dopa-services.jrc.ec.europa.eu/services/estation/get_recent_fires?minx=%s&miny=%s&maxx=%s&maxy=%s&srid=%s&from_time=%s",
						env.getMinX(), env.getMinY(), env.getMaxX(),
						env.getMaxY(), 4326,
						df.format(mostRecent).replace(" ", "%20"));

		System.out.println(url);

		String readJson = readJsonFromUrl(url);

		JsonParser parser = new JsonParser();
		JsonObject obj = (JsonObject) parser.parse(readJson);
		JsonArray arr = obj.get("records").getAsJsonArray();

		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
				.create();

		Type collectionType = new TypeToken<List<ActiveFire>>() {
		}.getType();

		List<ActiveFire> x = gson.fromJson(arr, collectionType);

		System.out.println("Fires retrieved from url: " + url);

		return x;
	}
}
