package cloneMatchingAlgorithms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Clone;
import database.Function;
import database.Functions;
import database.ToolsDB;

public class NearMatcher implements CloneMatcher {
	
	public String toString() {
		return "Near Match.  Tolerence: " + this.tol;
	}
	
	private Connection conn;
	private PreparedStatement stmt;
	
	private long toolid;
	private int tol;
	
	public NearMatcher(int toolid, String init) throws IllegalArgumentException, SQLException, NumberFormatException {
		this.toolid = toolid;
		String [] options = init.split("\\s+");
		if(options.length != 1)
			throw new IllegalArgumentException("Should take 1 parameter.");
		this.tol = Integer.parseInt(options[0]);
		init();
	}
	
	private void init() throws SQLException {
		String sql = "SELECT 1 FROM " + CloneMatcher.getTableName(this.toolid) + " where type1 = ? and name1 = ? and "
				+ "startline1 >= ? and startline1 <= ? and "
				+ "endline1 >= ? and endline1 <= ? and "
				+ "type2 = ? and name2 = ? and "
				+ "startline2 >= ? and startline2 <=? and "
				+ "endline2 >= ? and endline2 <= ?";
		this.conn = ToolsDB.getConnection();
		this.stmt = conn.prepareStatement(sql);
	}
	
	@Override
	public boolean isDetected(Clone clone) throws SQLException {
		Clone alt = new Clone(clone.getFunction_id_two(), clone.getFunction_id_one());
		return isDetected_helper(clone) || isDetected_helper(alt);
	}
	
	private boolean isDetected_helper(Clone clone) throws SQLException {
		boolean retval = false;
		Function f1 = Functions.get(clone.getFunction_id_one());
		Function f2 = Functions.get(clone.getFunction_id_two());
		
		stmt.setString(1, f1.getType());
		stmt.setString(2, f1.getName());
		stmt.setInt(3, f1.getStartline()-tol);
		stmt.setInt(4, f1.getStartline()+tol);
		stmt.setInt(5, f1.getEndline()-tol);
		stmt.setInt(6, f1.getEndline()+tol);
		
		stmt.setString(7, f2.getType());
		stmt.setString(8, f2.getName());
		stmt.setInt(9, f2.getStartline()-tol);
		stmt.setInt(10, f2.getStartline()+tol);
		stmt.setInt(11, f2.getEndline()-tol);
		stmt.setInt(12, f2.getEndline()+tol);
		
		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			retval = true;
		}
		rs.close();
		
		return retval;
	}

	@Override
	public void close() throws SQLException {
		stmt.close();
		conn.close();
		stmt = null;
		conn = null;
	}
	
}
