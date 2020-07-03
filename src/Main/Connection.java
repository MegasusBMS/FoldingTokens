package Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Connection extends Thread implements Runnable
{
	
	private JsonObject jo;
	private final String statsURL = "https://stats.foldingathome.org/api/team/254133";
	private JsonParser jp;
	public Connection()
	{
		this.jp = new JsonParser();
	}
	@Override
	public void run() 
	{
		while (true)
		{
			try
			{
				URL    url            = new URL( statsURL );
				HttpURLConnection conn= (HttpURLConnection) url.openConnection();    
				conn.setDoOutput( true );
				conn.setInstanceFollowRedirects( false );
				conn.setRequestMethod( "GET" );
				conn.connect();
				
				@SuppressWarnings("unused")
				int responseCode = conn.getResponseCode();
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
		
				while ((inputLine = in.readLine()) != null) 
				{
					response.append(inputLine+"\n");
				}
				in.close();
				
				synchronized(this)
				{
					jo = jp.parse(response.toString()).getAsJsonObject();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000*60*10);
			} catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public List<String> getPoints(String name) 
	{
		if (name.length() > 0)
		{
			synchronized(this)
			{	
				if(jo==null)
					return null;
				JsonArray ja = jo.get("donors").getAsJsonArray();
				int index = 1;
				for (JsonElement je:ja)
				{
					JsonObject jo2 = je.getAsJsonObject();
					if (jo2.get("name").getAsString().toLowerCase().startsWith(name.toLowerCase()))
					{
						return jsonToStats(jo2,index,ja.size());
					}
					index++;
				}
			}
		}
		return null;
	}
	private List<String> jsonToStats(JsonObject jo2,int index,int totalSize) 
	{
		List<String> toReturn = new ArrayList<String>();
		toReturn.add(jo2.get("credit").getAsString());
		toReturn.add(jo2.get("wus").getAsString());
		toReturn.add(index+"");
		toReturn.add(totalSize+ "");
		toReturn.add(jo2.get("name").getAsString());
		return toReturn;
	}
}