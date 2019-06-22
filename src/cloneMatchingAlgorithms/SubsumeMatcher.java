package cloneMatchingAlgorithms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Clone;
import database.Function;
import database.Functions;
import database.ToolsDB;

public class SubsumeMatcher implements CloneMatcher {

	public String toString() {
		String str = "";
		str += "Subsume.  IntTolerence = " + int_tolerence + " RatioTolerence = " + ratio_tolerence;
		return str;
	}
	
	private Connection conn;
	private PreparedStatement stmt;
	
	private long toolid;
	private Integer int_tolerence = null;
	private Double ratio_tolerence = null;
	
	
	public SubsumeMatcher(long toolid, String init) throws SQLException {
		this.toolid = toolid;
		
		String [] options = init.split("\\s+");
		if(options.length != 1 && options.length != 3)
			throw new IllegalArgumentException("Should take 1 or 3 parameters.");
		
		this.int_tolerence = null;
		this.ratio_tolerence = null;
		
		if(options.length == 3) {
			if(options[1].equals("line")) {
				this.int_tolerence = Integer.parseInt(options[2]);
			} else if (options[1].equals("ratio")) {
				this.ratio_tolerence = Double.parseDouble(options[2]);
			} else
				throw new IllegalArgumentException("Illegal option: " + options[1]);
		}

		init();
	}
	
	private void init() throws SQLException {
		String sql = "SELECT 1 FROM " + CloneMatcher.getTableName(this.toolid) + " where "
				+ "type1 = ? and name1 = ? and startline1 <= ? and endline1 >= ? and "
				+ "type2 = ? and name2 = ? and startline2 <= ? and endline2 >= ?";
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
		
		int one_tolerence;
		int two_tolerence;
		if(int_tolerence != null) {
			one_tolerence = int_tolerence;
			two_tolerence = int_tolerence;
		} else {// (ratio_tolerence != null) {
			one_tolerence = (int) Math.ceil((f1.getEndline() - f1.getStartline() + 1) * ratio_tolerence);
			two_tolerence = (int) Math.ceil((f2.getEndline() - f2.getStartline() + 1) * ratio_tolerence);
		}
		
		stmt.setString(1, f1.getType());
		stmt.setString(2, f1.getName());
		stmt.setInt(3, f1.getStartline() + one_tolerence);
		stmt.setInt(4, f1.getEndline() - one_tolerence);
		
		stmt.setString(5, f2.getType());
		stmt.setString(6, f2.getName());
		stmt.setInt(7, f2.getStartline() + two_tolerence);
		stmt.setInt(8, f2.getEndline() - two_tolerence);
		
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
	}

}
