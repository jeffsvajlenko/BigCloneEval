package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Functions {
	
	public static Function get(long id) throws SQLException {
		String sql = "SELECT id, name, type, startline, endline, normalized_size FROM functions WHERE id = " + id;
		Function function = null;
		
		Connection conn = BigCloneBenchDB.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			function = new Function(rs.getLong("id"), rs.getString("name"), rs.getString("type"), rs.getInt("startline"), rs.getInt("endline"), rs.getInt("normalized_size"));
		}
		rs.close();
		stmt.close();
		conn.close();
		
		return function;
	}
	
}
