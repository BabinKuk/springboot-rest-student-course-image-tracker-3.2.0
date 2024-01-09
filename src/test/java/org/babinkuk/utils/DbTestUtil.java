package org.babinkuk.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbTestUtil {
	
	public static final Logger log = LogManager.getLogger(DbTestUtil.class);
	
	private DbTestUtil() {}
	 
    public static void resetAutoIncrementColumns(
    		ApplicationContext applicationContext,
    		String resetSqlTemplate,
    		String... tableNames) throws SQLException {
		DataSource dataSource = applicationContext.getBean(DataSource.class);
		//String resetSqlTemplate = getResetSqlTemplate(applicationContext);
		log.info(resetSqlTemplate);
		try (Connection dbConnection = dataSource.getConnection()) {
		    //Create SQL statements that reset the auto increment columns and invoke 
			//the created SQL statements.
			for (String resetSqlArgument: tableNames) {
				log.info(resetSqlArgument);
				try (Statement statement = dbConnection.createStatement()) {
						String resetSql = String.format(resetSqlTemplate, resetSqlArgument);
						statement.execute(resetSql);
					}
				}
			}
		}
 
//    private static String getResetSqlTemplate(ApplicationContext applicationContext) {
//        //Read the SQL template from the properties file
//        Environment environment = applicationContext.getBean(Environment.class);
//        return environment.getRequiredProperty(sqlResetTemplate);
//    }
}
