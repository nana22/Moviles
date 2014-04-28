package Datos;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import logicaDeNegocios.Persona;
import logicaDeNegocios.Propiedad;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class Consultas {
    
    private static synchronized String consultar(String uri) {
        String query = "";
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            InputStream data = connection.getInputStream();
            query = getStringFromInputStream(data);
            data.close();
            connection.disconnect();
	} catch (IOException e) {
            System.out.println("Error de conexión...");
	}
        return query;
    }
    
    private static synchronized void submit(String json, String uri) throws IOException {
        try {
            HttpPost post = new HttpPost(uri);
            StringEntity input = new StringEntity(json, "utf-8");
            input.setContentType("application/json");
            post.setEntity(input);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Consultas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error...");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Error...");
                }
            }
        }
        return sb.toString();
    }
    
    public List<Propiedad> consultarPropiedades(){
        String query = consultar("http://localhost:3000/verPropiedades");
        List<Propiedad> propiedades = null;
        if (!query.equals("")) {
            propiedades = new Gson().fromJson(query, new TypeToken<List<Propiedad>>(){}.getType());
        }
        return propiedades;
    }
    
    public List<Persona> consultarDueños(){
        String query = consultar("http://localhost:3000/verDuenios");
        List<Persona> dueños = null;
        if (!query.equals("")) {
            dueños = new Gson().fromJson(query, new TypeToken<List<Persona>>(){}.getType());
        }
        return dueños;
    }
    
    public Persona consultarDueñoXPropiedad(String pFbDueño) {
        String query = consultar("http://localhost:3000/verDueniosXPropiedad/" + pFbDueño);
        List<Persona> dueño = null;
        if (!query.equals("")) {
            dueño = new Gson().fromJson(query, new TypeToken<List<Persona>>(){}.getType());
        }
        return dueño.get(0);
    }
    
    public List<Propiedad> consultarPropiedadesXDuenio(String pFacebook) {
        String query = consultar("http://localhost:3000/verPropiedadesXDuenio/" + pFacebook);
        List<Propiedad> propiedades = null;
        if (!query.equals("")) {
            propiedades = new Gson().fromJson(query, new TypeToken<List<Propiedad>>(){}.getType());
        }
        return propiedades;
    }
    
    public void submitPropiedad(Propiedad pPropiedad) throws IOException {
        submit(new Gson().toJson(pPropiedad), "http://localhost:3000/addPropiedad");
    }
    
    public void submitDueño(Persona pDueño) throws IOException {
        submit(new Gson().toJson(pDueño), "http://localhost:3000/addPerson");
    }
}