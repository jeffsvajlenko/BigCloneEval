package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class BigCloneBenchDB {
	
	private static BigCloneBenchDB instance = null;
	
	private BoneCP connectionPool = null;
	
	public static void reinit(String name) throws SQLException {
		if(instance != null) {
			instance.connectionPool.close();
			instance = null;
		}
		instance = new BigCloneBenchDB();
	}
	
	private static BigCloneBenchDB getConnectionPool() throws SQLException {
		if(instance == null)
			instance = new BigCloneBenchDB();
		return instance;
	}
	
	private BigCloneBenchDB() throws SQLException {
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl("jdbc:h2:bigclonebenchdb/bcb");
		config.setUsername("sa");
		config.setPassword("");
		config.setMinConnectionsPerPartition(1);
		config.setMaxConnectionsPerPartition(10);
		config.setPartitionCount(1);
		connectionPool = new BoneCP(config);
	}
	
	public static Connection getConnection() throws SQLException {
		return BigCloneBenchDB.getConnectionPool().connectionPool.getConnection();
	}
	
	/**
	 * Opens a new private connection to the database (not in pool).  Your responsibility to close the connection.
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection getPrivateConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:bcb","sa","");
		return conn;
	}

	public static String getVersion() throws SQLException {
		String retval;
		Connection conn = BigCloneBenchDB.getConnection();
		String sql = "SELECT version FROM version LIMIT 1";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			retval = rs.getString("version");
		} else {
			retval = "unknown";
		}
		rs.close();
		stmt.close();
		conn.close();
		return retval;
	}
	
}
