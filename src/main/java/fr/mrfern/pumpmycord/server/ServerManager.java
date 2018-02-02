package fr.mrfern.pumpmycord.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.mrfern.pumpmycord.config.MySQLConnector;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ServerManager {
	
	private static ServerManager serverManager = new ServerManager();
	private ProxiedPlayer p;

	public static ServerManager getManager(ProxiedPlayer p) {
		serverManager.setP(p);
		return serverManager;
	}

	public ProxiedPlayer getP() {
		return p;
	}

	public void setP(ProxiedPlayer p) {
		this.p = p;
	}
	
	public boolean isBan(String serverName) {
		System.out.println(p.getUniqueId());	// r�cup�ration de l'UUID du joueur
		ResultSet listRS = new MySQLConnector().sendQuery("SELECT `ban_ID` FROM `player_ban` WHERE `player_UUID`='" + p.getUniqueId() +"'");	// commande pour r�cup�rer les ids de ban correspondant � ce UUID
		List<Integer> banIDList = new ArrayList<>();	// Instanciation de la liste de ban
		
		try {
			// r�cup�ration du contenu de la table
			while(listRS.next()) {
				try {
					// ajout � la liste de banID
					System.out.println(listRS.getInt("ban_ID"));
					banIDList.add(listRS.getInt("ban_ID"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}			
			if(banIDList.isEmpty()) {
				// si pas de ban avec cette uuid alors return false
				return false;
			}else {
				for (int banID : banIDList) {
					ResultSet banRS = new MySQLConnector().sendQuery("SELECT `author_UUID`, `author_name`, `ban_type`, `end` FROM `ban_list` WHERE id=" + banID);
					banRS.next();
					
					if(banRS.getBoolean("end")) {	// calcul end fait toutes les minutes
						ResultSet removeRS = new MySQLConnector().sendQuery("SELECT `player_UUID` FROM `player_ban` WHERE `ban_ID`=" + banID); // r�cup�ration de l'UUID du joueur banni
						removeRS.next();
						String removeUUID = removeRS.getString("player_UUID");						
						new MySQLConnector().sendUpdate("DELETE FROM `player_ban` WHERE `ban_ID`=" + banID);	// suppresion de la ligne correspondante � cette UUID dans la table player_ban
						new MySQLConnector().sendUpdate("UPDATE `ban_list` SET `player_UUID`='" + removeUUID + "' WHERE `id`=" + banID);	// update ligne dans ban_list
					
						// donc deban / v�rification du nom du serveur
						if(serverName.equals(banRS.getString("ban_type")) | banRS.getString("ban_type").equals("global")) {
							return false;
						}
					}else {
						// sinon ban
						if(serverName.equals(banRS.getString("ban_type")) | banRS.getString("ban_type").equals("global")) {
							return true;
						}
					}					
				}
				return false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean getBanIsGlobal() {
		System.out.println(p.getUniqueId());	// r�cup�ration de l'UUID du joueur
		ResultSet listRS = new MySQLConnector().sendQuery("SELECT `ban_ID` FROM `player_ban` WHERE `player_UUID`='" + p.getUniqueId() +"'");	// commande pour r�cup�rer les ids de ban correspondant � ce UUID
		List<Integer> banIDList = new ArrayList<>();	// Instanciation de la liste de ban
		
		try {
			while(listRS.next()) {
				try {
					// ajout � la liste de banID
					System.out.println(listRS.getInt("ban_ID"));
					banIDList.add(listRS.getInt("ban_ID"));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				if(banIDList.isEmpty()) {
					// si pas de ban avec cette uuid alors return false
					return false;
				}else {
					for (int banID : banIDList) {
						ResultSet banRS = new MySQLConnector().sendQuery("SELECT `ban_type` FROM `ban_list` WHERE id=" + banID);	// r�cup�ration du type de ban
						banRS.next();
						if(banRS.getString("ban_type").equals("global")) {
							// si global alors return true isglobal sinon on continue de check
							return true;
						}
					}
					return false;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;		
	}
	
	public String getAuthor(String serverName) {
		
		ResultSet listRS = new MySQLConnector().sendQuery("SELECT `ban_ID` FROM `player_ban` WHERE `player_UUID`='" + p.getUniqueId() +"'");	// commande pour r�cup�rer les ids de ban correspondant � ce UUID
		try {
			// r�cup�ration du contenu de la table
			while(listRS.next()) {
				try {
					// ajout � la liste de banID
					int banID = listRS.getInt("ban_ID");
					ResultSet rs = new MySQLConnector().sendQuery("SELECT `author_UUID`, `author_name`, `ban_type`, `raison`, `end` FROM `ban_list` WHERE `id`=" + banID); // r�cup�ration de l'UUID du joueur banni
					rs.next();
					
					//String author_UUID = rs.getString("author_UUID");
					String author_name = rs.getString("author_name");
					String ban_type = rs.getString("ban_type");
					//String raison = rs.getString("raison");
					boolean end = rs.getBoolean("end");
					
					if(end) {	// calcul end fait toutes les minutes
						ResultSet removeRS = new MySQLConnector().sendQuery("SELECT `player_UUID` FROM `player_ban` WHERE `ban_ID`=" + banID); // r�cup�ration de l'UUID du joueur banni
						removeRS.next();
						String removeUUID = removeRS.getString("player_UUID");						
						new MySQLConnector().sendUpdate("DELETE FROM `player_ban` WHERE `ban_ID`=" + banID);	// suppresion de la ligne correspondante � cette UUID dans la table player_ban
						new MySQLConnector().sendUpdate("UPDATE `ban_list` SET `player_UUID`='" + removeUUID + "' WHERE `id`=" + banID);	// update ligne dans ban_list
					
						// donc deban / v�rification du nom du serveur
						if(serverName.equals(ban_type) | "global".equals(ban_type)) {
							return "none";
						}
					}else {
						// sinon ban / v�rification du nom du serveur
						if(serverName.equals(ban_type) | "global".equals(ban_type)) {
							return author_name;
						}
					}					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "none";
	}
	
	public String getAuthorUUID(String serverName) {
		ResultSet listRS = new MySQLConnector().sendQuery("SELECT `ban_ID` FROM `player_ban` WHERE `player_UUID`='" + p.getUniqueId() +"'");	// commande pour r�cup�rer les ids de ban correspondant � ce UUID
		try {
			// r�cup�ration du contenu de la table
			while(listRS.next()) {
				try {
					// ajout � la liste de banID
					int banID = listRS.getInt("ban_ID");
					ResultSet rs = new MySQLConnector().sendQuery("SELECT `author_UUID`, `author_name`, `ban_type`, `raison`, `end` FROM `ban_list` WHERE `id`=" + banID); // r�cup�ration de l'UUID du joueur banni
					rs.next();
					
					String author_UUID = rs.getString("author_UUID");
					//String author_name = rs.getString("author_name");
					String ban_type = rs.getString("ban_type");
					//String raison = rs.getString("raison");
					boolean end = rs.getBoolean("end");
					
					if(end) {	// calcul end fait toutes les minutes
						ResultSet removeRS = new MySQLConnector().sendQuery("SELECT `player_UUID` FROM `player_ban` WHERE `ban_ID`=" + banID); // r�cup�ration de l'UUID du joueur banni
						removeRS.next();
						String removeUUID = removeRS.getString("player_UUID");						
						new MySQLConnector().sendUpdate("DELETE FROM `player_ban` WHERE `ban_ID`=" + banID);	// suppresion de la ligne correspondante � cette UUID dans la table player_ban
						new MySQLConnector().sendUpdate("UPDATE `ban_list` SET `player_UUID`='" + removeUUID + "' WHERE `id`=" + banID);	// update ligne dans ban_list
					
						// donc deban / v�rification du nom du serveur
						if(serverName.equals(ban_type) | "global".equals(ban_type)) {
							return "none";
						}
					}else {
						// sinon ban / v�rification du nom du serveur
						if(serverName.equals(ban_type) | "global".equals(ban_type)) {
							return author_UUID;
						}
					}					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "none";
	}

	public String getRaison() {
		// TODO Auto-generated method stub
		return "no raison";
	}

	public int getDay() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHour() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMinute() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getYear_end() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMonth_end() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getDay_end() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getHour_end() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMinute_end() {
		// TODO Auto-generated method stub
		return 0;
	}	
}
