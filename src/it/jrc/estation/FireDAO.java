package it.jrc.estation;

import it.jrc.estation.domain.ActiveFire;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import org.wcs.smart.hibernate.HibernateManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class FireDAO {

	private static final String UTF_8 = "UTF-8";

	public static Collection<ActiveFire> getSomeFires(double minx, double miny, double maxx,
			double maxy) {
		String url;
		url = String
				.format("http://dopa-services.jrc.ec.europa.eu/services/estation/get_5day_fires?minx=%s&miny=%s&maxx=%s&maxy=%s",
						minx, miny, maxx, maxy);
		
		String readJson = readJsonFromUrl(url);
		
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject)parser.parse(readJson);
        JsonArray arr = obj.get("records").getAsJsonArray();
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();

		Type collectionType = new TypeToken<Collection<ActiveFire>>() {
		}.getType();


		Collection<ActiveFire> x = gson.fromJson(arr, collectionType);
			
		System.out.println("Fires retrieved from url: " + url);


		return x;
	}

	public static void main(String[] args) {

		FireDAO dao = new FireDAO();

		Collection<ActiveFire> someFires = dao.getSomeFires(0d, 0d, 0d, 0d);

		FireHibernateManager.testClazz = ActiveFire.class;
		FireHibernateManager.setDatabaseParameter("E:/smart_environment/smart/data/database/smartdb");
		FireHibernateManager.setUserName("smart_admin", "smart_derby");
		for (ActiveFire activeFire : someFires) {

			boolean worked = FireHibernateManager.saveActiveFire(activeFire, FireHibernateManager.openSession());

			if (! worked) {
				break;
			}

		}
		// String json = readUrl("http://www.javascriptkit.com/"
		// + "dhtmltutors/javascriptkit.json");

		// Gson gson = new Gson();
		// Page page = gson.fromJson(json, Page.class);
		//
		// System.out.println(page.title);
		// for (Item item : page.items)
		// System.out.println("    " + item.title);

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
}
