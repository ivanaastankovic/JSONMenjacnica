package rs.ac.bg.fon.ai.JSONMenjacnica;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;



public class Main2 {

	private static final String BASE_URL = "http://api.currencylayer.com";
	private static final String API_KEY = "1c4962ff452a03d0951deb78e452e6f5";
	
	public static void main(String[] args) {
		Transakcija transakcija1 = new Transakcija();
		transakcija1.setPocetniIznos(100);
		transakcija1.setIzvornaValuta("USD");
		transakcija1.setKrajnjaValuta("EUR");
		transakcija1.setDatumTransakcije(new GregorianCalendar(2021,2,24).getTime());
		
		Transakcija transakcija2 = new Transakcija();
		transakcija2.setPocetniIznos(100);
		transakcija2.setIzvornaValuta("USD");
		transakcija2.setKrajnjaValuta("CHF");
		transakcija2.setDatumTransakcije(new GregorianCalendar(2021,2,24).getTime());
		
		Transakcija transakcija3 = new Transakcija();
		transakcija3.setPocetniIznos(100);
		transakcija3.setIzvornaValuta("USD");
		transakcija3.setKrajnjaValuta("CAD");
		transakcija3.setDatumTransakcije(new GregorianCalendar(2021,2,24).getTime());
		
		List<Transakcija> lista = new LinkedList<Transakcija>();
		lista.add(izracunajIznos(transakcija1));
		lista.add(izracunajIznos(transakcija2));
		lista.add(izracunajIznos(transakcija3));
		
		try(FileWriter file = new FileWriter("ostale_transakcije.json")){
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(lista, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Transakcija izracunajIznos(Transakcija transakcija) {
		try {
			Long ts = transakcija.getDatumTransakcije().getTime() / 1000;
			
			URL url = new URL(BASE_URL + "/live?access_key=" +
					API_KEY + "&source=" + transakcija.getIzvornaValuta()
					+ "&currencies=" + transakcija.getKrajnjaValuta()
					+ "&timestamp=" + ts);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonObject res = gson.fromJson(reader, JsonObject.class);
			
			if (res.get("success").getAsBoolean()) {
				transakcija.setKonvertovaniIznos(
						res.get("quotes").getAsJsonObject().get(transakcija.getIzvornaValuta() + transakcija.getKrajnjaValuta()).getAsDouble()
								* transakcija.getPocetniIznos());
			}			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return transakcija;
	}

}
