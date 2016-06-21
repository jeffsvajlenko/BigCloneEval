package cloneMatchingAlgorithms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Clone;
import database.Function;
import database.Functions;
import database.ToolsDB;

// Tool reports a clone that covers a given ratio of the clone.  If tolerance is not null, then the detected clone can not exceed this tolerence past the original boundaries.

public class CoverageMatcher implements CloneMatcher {
	
	private Connection conn;
	private PreparedStatement stmt;
	
	private long toolid;
	private Integer tolerence;
	private Double coverage;
	private Double dtolerence;
	
	public String toString() {
		String str = "";
		str += "Coverage Matcher.  Coverage Ratio = " + coverage + ", minimum raito is of reference clone: " + dtolerence;
		return str;
	}
	
	public CoverageMatcher(long toolid, String init) throws IllegalArgumentException, SQLException {
		this.toolid = toolid;
		String [] options = init.split("\\s+");
		if(options.length != 1 && options.length != 3)
			throw new IllegalArgumentException("Should take 1 or 3 parameters.");
		
		this.coverage = Double.parseDouble(options[0]);
		this.tolerence = null;
		this.dtolerence = null;
		
		if(options.length == 3) {
			if(options[1].equals("line")) {
				this.tolerence = Integer.parseInt(options[2]);
			} else if (options[1].equals("ratio")) {
				this.dtolerence = Double.parseDouble(options[2]);
			} else
				throw new IllegalArgumentException("Illegal option: " + options[1]);
		}
		init();
	}
	
	public CoverageMatcher(long toolid, double coverage, Integer tolerence, Double dtolerence) throws SQLException {
		this.toolid = toolid;
		this.tolerence = tolerence;
		this.coverage = coverage;
		this.dtolerence = dtolerence;
		init();
	}
	
	private void init() throws SQLException {
		String sql = "SELECT 1 FROM " + CloneMatcher.getTableName(this.toolid) + " where type1 = ? and name1 = ? and "
				+ "(least(?,endline1)-greatest(?,startline1)+1)/? >= " + coverage + " "
				+ "and type2 = ? and name2 = ? and "
				+ "(least(?,endline2)-greatest(?,startline2)+1)/? >= " + coverage;
		if(tolerence != null) {
			sql += " AND startline1 >= ? AND endline1 <= ? AND startline2 >= ? AND endline2 <= ?";
		} else if(dtolerence != null) {
			sql += " AND (least(?,endline1)-greatest(?,startline1)+1)/(1.0*(endline1-startline1+1)) >= " + dtolerence;
			sql += " AND (least(?,endline2)-greatest(?,startline2)+1)/(1.0*(endline2-startline2+1)) >= " + dtolerence;
		}
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
		stmt.setInt(3, f1.getEndline());
		stmt.setInt(4, f1.getStartline());
		stmt.setDouble(5, f1.getEndline() - f1.getStartline() + 1);
		
		stmt.setString(6, f2.getType());
		stmt.setString(7, f2.getName());
		stmt.setInt(8, f2.getEndline());
		stmt.setInt(9, f2.getStartline());
		stmt.setDouble(10, f2.getEndline() - f2.getStartline() + 1);
		
		if(tolerence != null) {
			stmt.setInt(12, f1.getStartline() - tolerence);
			stmt.setInt(13, f1.getEndline() + tolerence);
			stmt.setInt(14, f2.getStartline() - tolerence);
			stmt.setInt(15, f2.getEndline() + tolerence);
		} else if(dtolerence != null) {
			stmt.setInt(12, f1.getEndline());
			stmt.setInt(13, f1.getStartline());
			stmt.setInt(14, f2.getEndline());
			stmt.setInt(15, f2.getStartline());
		}
		
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
