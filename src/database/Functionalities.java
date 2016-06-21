package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class Functionalities {
	public static Set<Long> getFunctionalityIds() throws SQLException {
		String sql = "select id from functionalities";
		Set<Long> ids = new HashSet<Long>();
		
		Connection conn = BigCloneBenchDB.getConnection();
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while(rs.next()) ids.add(rs.getLong("id"));
		rs.close();
		statement.close();
		conn.close();
		
		return ids;
	}
	
	public static Functionality getFunctinality(long functionality_id) throws SQLException {
		Connection conn = BigCloneBenchDB.getConnection();
		Statement stmt = conn.createStatement();
		
		String sql = "SELECT id, name, description, search_heuristic FROM functionalities WHERE id = " + functionality_id;
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		long id = rs.getLong(1);
		String name = rs.getString(2);
		String desc = rs.getString(3);
		String heuristic = rs.getString(4);
		rs.close();
		stmt.close();
		conn.close();
		return new Functionality(id, name, desc, heuristic);
	}
	
}
