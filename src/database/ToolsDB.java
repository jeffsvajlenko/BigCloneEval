package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class ToolsDB {
	
	private static ToolsDB instance = null;
	
	private BoneCP connectionPool = null;
	
	public static void reinit() throws SQLException {
		if(instance != null) {
			instance.connectionPool.close();
			instance = null;
		}
		instance = new ToolsDB();
	}
	
	private static ToolsDB getConnectionPool() throws SQLException {
		if(instance == null)
			instance = new ToolsDB();
		return instance;
	}
	
	private ToolsDB() throws SQLException {
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl("jdbc:h2:toolsdb/tools");
		config.setUsername("sa");
		config.setPassword("");
		config.setMinConnectionsPerPartition(1);
		config.setMaxConnectionsPerPartition(10);
		config.setPartitionCount(1);
		connectionPool = new BoneCP(config);
	}
	
	public static Connection getConnection() throws SQLException {
		return ToolsDB.getConnectionPool().connectionPool.getConnection();
	}
	
	/**
	 * Opens a new private connection to the database (not in pool).  Your responsibility to close the connection.
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection getPrivateConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:tools","sa","");
		return conn;
	}
	
}
