package database;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Clones {
	
	public static long numClones(long id) throws SQLException {
		long retval = 0;
		String sql = "SELECT count(1) FROM tool_" + id + "_clones";
		Connection conn = ToolsDB.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next())
			retval = rs.getLong(1);
		stmt.close();
		conn.close();
		return retval;
	}
	
	public static int clearClones(long id) throws SQLException {
		Connection conn = ToolsDB.getConnection();
		String sql = "DELETE FROM tool_" + id + "_clones";
		Statement stmt = conn.createStatement();
		int retval = stmt.executeUpdate(sql);
		stmt.close();
		conn.close();
		return retval;
	}
	
	public static long importClones(long id, Path path) throws IOException, SQLException {
		// Import
		Connection conn = ToolsDB.getConnection();
		String sql = "INSERT INTO tool_" + id + "_clones SELECT (type1, name1, startline1, endline1, type2, name2, startline2, endline2) FROM csvread('" + path.toString() + "')";
		Statement stmt = conn.createStatement();
		long retval = stmt.executeUpdate(sql);
		conn.close();
		return retval;
	}
	
}
