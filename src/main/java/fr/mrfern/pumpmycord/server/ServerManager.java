package fr.mrfern.pumpmycord.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fr.mrfern.pumpmycord.config.MySQLConnector;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ServerManager {
	
	private ProxiedPlayer p;
	private HashMap<String,BanData> banDataList = new HashMap<>();

	public ServerManager(ProxiedPlayer player) {
		setP(player);
		ResultSet listRS = new MySQLConnector().sendQuery("SELECT `ban_ID` FROM `ban_player` WHERE `player_UUID`='" + p.getUniqueId() +"'");	// commande pour r�cup�rer les ids de ban correspondant � ce UUID
		List<Integer> banIDList = new ArrayList<>();	// Instanciation de la liste de ban
		
		// r�cup�ration du contenu de la table
		try {
			while(listRS.next()) {
				try {
					// ajout � la liste de banID
					banIDList.add(listRS.getInt("ban_ID"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(!banIDList.isEmpty()) {
				// si ban list vide alors pas ban
				for (int banID : banIDList) {
					ResultSet banRS = new MySQLConnector().sendQuery("SELECT `author_UUID`, `author_name`, `ban_type`, `raison`, `bantime_day`, `bantime_hour`, `bantime_minut`, `end_bantime_year`, `end_bantime_month`, `end_bantime_day`, `end_bantime_hour`, `end_bantime_minut`, `player_UUID`, `end` FROM `ban_list` WHERE `id`=" + banID);
					banRS.next();
					
					boolean end = banRS.getBoolean("end");
					
					if(!end) {
						BanData banData = new BanData();
						
						String banType = banRS.getString("ban_type");
						
						banData.setAuthor(banRS.getString("author_name")); 	// set author name dans ban Data
						banData.setAuthor_UUID(banRS.getString("author_UUID")); 	// set author UUID dans ban Data
						
						banData.setBanType(banType); 	// set ban type dans ban Data
						
						banData.setRaison(banRS.getString("raison")); 	// set raison dans ban Data
						
						// set temps de ban dans ban Data
						banData.setDay(banRS.getInt("bantime_day"));
						banData.setHour(banRS.getInt("bantime_hour"));
						banData.setMinute(banRS.getInt("bantime_minut"));
						
						//set date de fin de ban dans ban Data
						banData.setYear_end(banRS.getInt("end_bantime_year"));
						banData.setMonth_end(banRS.getInt("end_bantime_month"));
						banData.setDay_end(banRS.getInt("end_bantime_day"));
						banData.setHour_end(banRS.getInt("end_bantime_hour"));
						banData.setMinute_end(banRS.getInt("end_bantime_minut"));
						
						banDataList.put(banType, banData); // ajout � la map les infos sur le ban
					}							
				}
				
			}
						
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static ServerManager getManager(ProxiedPlayer p) {	
		return new ServerManager(p);
	}

	public ProxiedPlayer getP() {
		return p;
	}

	public void setP(ProxiedPlayer p) {
		this.p = p;
	}
	
	public boolean isBan(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return true;
			}
		}
		return false;
	}
	
	public boolean getBanIsGlobal() {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals("global")) {	// test ban global
				return true;
			}
		}
		return false;
	}
	
	public String getAuthor(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getAuthor();
			}
		}
		return "none";
	}
	
	public String getAuthorUUID(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getAuthor_UUID();
			}
		}
		return "none";
	}

	public String getRaison(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getRaison();
			}
		}
		return "no raison";
	}

	public int getDay(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getDay();
			}
		}
		return 0;
	}

	public int getHour(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getHour();
			}
		}
		return 0;
	}

	public int getMinute(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getMinute();
			}
		}
		return 0;
	}

	public int getYear_end(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getYear_end();
			}
		}
		return 0;
	}

	public int getMonth_end(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getMonth_end();
			}
		}
		return 0;
	}

	public int getDay_end(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getDay_end();
			}
		}
		return 0;
	}

	public int getHour_end(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getHour_end();
			}
		}
		return 0;
	}

	public int getMinute_end(String serverName) {
		for (Entry<String, BanData> entry : banDataList.entrySet()) {	//parcours de la liste de ban
			if(entry.getKey().equals(serverName) | entry.getKey().equals("global")) {	// test nom du serveur ou ban global
				return entry.getValue().getMinute_end();
			}
		}
		return 0;
	}

	public HashMap<String,BanData> getBanDataList() {
		return banDataList;
	}

	public void setBanDataList(HashMap<String,BanData> banDataList) {
		this.banDataList = banDataList;
	}	
}
