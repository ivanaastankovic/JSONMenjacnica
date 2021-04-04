package rs.ac.bg.fon.ai.JSONMenjacnica;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Main1 {

	private static final String BASE_URL= "http://api.currencylayer.com";
	private static final String API_KEY ="2e4baadf5c5ae6ba436f53ae5558107f";
	private static final String SOURCE = "USD";
	private static final String CURRENCIES="CAD"; 
	
	public static void main(String[] args) {
		try {
			Transakcija transakcija = new Transakcija();
			transakcija.setPocetniIznos(61);
			transakcija.setIzvornaValuta("USD");
			transakcija.setKrajnjaValuta("CAD");
			transakcija.setDatumTransakcije(new Date());
;			Gson gson = new Gson();
			
			URL url = new URL(BASE_URL+"/live?access_key="+API_KEY+"&source="+SOURCE+"&currencies"+CURRENCIES);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			JsonObject result = gson.fromJson(reader,JsonObject.class);
		//	System.out.println(result);
			
			if(result.get("success").getAsBoolean()) {
				double kurs= result.get("quotes").getAsJsonObject().get("USDCAD").getAsDouble();
				transakcija.setKonvertovaniIznos(kurs*transakcija.getPocetniIznos());
		//		System.out.println(transakcija.getKonvertovaniIznos());

				try(FileWriter file=new FileWriter("prva_transakcija.json")){
					Gson gson1 = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
					gson.toJson(transakcija,file);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
