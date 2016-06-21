package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class Tools {
	
	public static boolean exists(long id) throws SQLException {
		Tool tool = getTool(id);
		if(tool == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void dropall() throws SQLException {		
		Connection conn = ToolsDB.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "DROP ALL OBJECTS";
		stmt.execute(sql);
		stmt.close();
		conn.close();
	}
	
	public static void init() throws SQLException {
		dropall();
		String sql = "CREATE TABLE tools ( name character varying NOT NULL, description character varying NOT NULL, id identity NOT NULL);";
		Connection conn = ToolsDB.getConnection();
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
		conn.close();
	}
	
	public static boolean deleteToolAndData(long id) throws SQLException {
		
		// Remove Tool
		String sql = "DELETE FROM tools WHERE id = " + id;
		Connection conn = ToolsDB.getConnection();
		Statement stmt = conn.createStatement();
		int num = stmt.executeUpdate(sql);
		conn.close();
		if(num == 0) {
			return false;
		}
		
		// Remove Table
		sql = "DROP TABLE tool_" + id + "_clones";
		stmt.execute(sql);
		conn.close();
		return true;
	}
	
	public static List<Tool> getTools() throws SQLException {
		List<Tool> retval = new LinkedList<Tool>();
		String sql = "SELECT id, name, description FROM tools ORDER BY id";
		Connection conn = ToolsDB.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			retval.add(new Tool(rs.getLong(1), rs.getString(2), rs.getString(3)));
		}
		rs.close();
		stmt.close();
		conn.close();
		return retval;
	}
	
	public static Tool getTool(long id) throws SQLException {
		Tool retval;
		String sql = "SELECT id, name, description FROM tools WHERE id = " + id;
		Connection conn = ToolsDB.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			retval = new Tool(rs.getLong(1), rs.getString(2), rs.getString(3));
		} else {
			retval = null;
		}
		rs.close();
		stmt.close();
		conn.close();
		return retval;
	}
	
	public static long addTool(String name, String description) throws SQLException {
	// Add Tool
		String sql = "INSERT INTO tools (name, description) VALUES (?,?)";
		Connection conn = ToolsDB.getConnection();
		PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, name);
		stmt.setString(2, description);
		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();
		rs.next();
		long id = rs.getLong(1);
		rs.close();
		stmt.close();
		
	// Add Clones Table and its index
		Statement stmt2 = conn.createStatement();
		sql = "CREATE TABLE tool_" + id + "_clones (type1 character varying NOT NULL, name1 character varying NOT NULL, startline1 integer NOT NULL, endline1 integer NOT NULL, type2 character varying NOT NULL, name2 character varying NOT NULL, startline2 integer NOT NULL, endline2 integer NOT NULL);";
		stmt2.execute(sql);
		sql = "CREATE INDEX ON tool_" + id + "_clones (type1, name1, startline1, endline1, type2, name2, startline2, endline2)";
		stmt2.execute(sql);
		sql = "CREATE INDEX ON tool_" + id + "_clones (type1, name1, type2, name2)";
		stmt2.execute(sql);
		stmt2.close();
		
		conn.close();
		return id;
	}
	
}
