package simpleEconomy.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {
	private SimpleEconomy plugin;
	private Exception exception;
	private DateFormat dateFormat;
	private Date date;
	
	private File logFile;
	private FileWriter logFileWriter;
	private BufferedWriter logBufferWriter;
	
	private String path;
	
	public LogManager(SimpleEconomy plugin) {
		this.plugin = plugin;
		path = this.plugin.getLogPath();
		
		if((exception = loadConfig()) != null) {
			this.plugin.getLogger().error("Can't log simpleEconomy outputs ! Disabling of logging option.");
			this.plugin.getLogger().error(exception.getMessage());
			this.plugin.setEnableLogs(false);
		} else {
			this.plugin.getLogger().info("Transactions logging activated.");
		}
	}
	
	private Exception loadConfig() {		
		logFile = new File(path);
		
		try {
			if(!logFile.exists()){
    			logFile.createNewFile();
    		}
			logFileWriter = new FileWriter(logFile.getName(), true);
			logBufferWriter = new BufferedWriter(logFileWriter);
			
		} catch (IOException e) {
			return e;
		}
		
		dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		date = new Date();
		
		return null;
	}
	
	private Date getDateTime() {
		return date;
	}
	
	public void addLogTrace(String message) {
		try {
			logBufferWriter.write(dateFormat.format(getDateTime()) + " : " + message);
			logBufferWriter.newLine();
		} catch (IOException e) {
			this.plugin.getLogger().error(e.getMessage());
		}
	}
	
	public void closeLogFile() {
		try {
			logBufferWriter.close();
		} catch (IOException e) {
			this.plugin.getLogger().error(e.getMessage());
		}
	}
}
