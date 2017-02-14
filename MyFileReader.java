import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MyFileReader {
	private static final String FETCH = "data/fetch_NewsSite.csv";
	private static final String VISIT = "data/visit_NewsSite.csv";
	private static final String TOTAL = "data/urls_NewsSite_new.csv";
	private static final String STAT = "data/CrawlReport_NYTimes.txt";
	
	private Map<String, String> statusDetails;
	private Map<String, Integer> fetch_stat;
	private Map<String, Integer> statusCodes;
	private int totalExtracted;
	private Map<String, Integer> fileSize;
	private Map<String, Integer> contentType;
	private Set<String> uniqueURL;
	private int uniqueURL_in;
	private int uniqueURL_out;
	public MyFileReader() {
		this.statusDetails = new HashMap<String, String>();
		initStatusCodes();
		this.fetch_stat = new HashMap<String, Integer>();
		initFetch_stat();
		this.statusCodes = new HashMap<String, Integer>();
		this.totalExtracted = 0;
		this.fileSize = new HashMap<String, Integer>();
		initFileSize();
		this.contentType = new HashMap<String, Integer>();
		this.uniqueURL = new HashSet<String>();
		this.uniqueURL_in = 0;
		this.uniqueURL_out = 0;
	}
	
	private void initFileSize() {
		fileSize.put("< 1KB", 0);
		fileSize.put("1KB ~ <10KB", 0);
		fileSize.put("10KB ~ <100KB", 0);
		fileSize.put("100KB ~ <1MB", 0);
		fileSize.put(">= 1MB", 0);
	}

	private void initFetch_stat() {
		fetch_stat.put("fetches attempted", 0);
		fetch_stat.put("fetches succeeded", 0);
		fetch_stat.put("fetches aborted", 0);
		fetch_stat.put("fetches failed", 0);
	}

	public void initStatusCodes() {
		
		statusDetails.put("100", "Continue");
		statusDetails.put("101", "Switching Protocols");
		statusDetails.put("200", "Successful");
		statusDetails.put("201", "Created");
		statusDetails.put("202", "Accepted");
		statusDetails.put("203", "Non-Authorative Information");
		statusDetails.put("204", "No Content");
		statusDetails.put("205", "Reset Content");
		statusDetails.put("206", "Partial Content");
		statusDetails.put("300", "Multiple Choices");
		statusDetails.put("301", "Moved Permanently");
		statusDetails.put("302", "Moved Temporarily");
		statusDetails.put("303", "See Other");
		statusDetails.put("304", "Not Modified");
		statusDetails.put("305", "Use Proxy");
		statusDetails.put("307", "Temporary Redirect");
		statusDetails.put("400", "Bad Request");
		statusDetails.put("401", "Unauthorized");
		statusDetails.put("402", "Payment Required");
		statusDetails.put("403", "Forbidden");
		statusDetails.put("404", "Not Found");
		statusDetails.put("405", "Method Not Allowed");
		statusDetails.put("406", "Not Acceptable");
		statusDetails.put("407", "Proxy Authentication Required");
		statusDetails.put("408", "Request Timeout");
		statusDetails.put("409", "Conflict");
		statusDetails.put("410", "Gone");
		statusDetails.put("411", "Length Required");
		statusDetails.put("412", "Precondition Failed");
		statusDetails.put("413", "Request Entity Too Long");
		statusDetails.put("414", "Request-URI Too Long");
		statusDetails.put("415", "Unsupported Media Type");
		statusDetails.put("500", "Internal Server Error");
		statusDetails.put("501", "Not Implemented");
		statusDetails.put("502", "Bad Gateway");
		statusDetails.put("503", "Service Unavailable");
		statusDetails.put("504", "Gateway Timeout");
		statusDetails.put("505", "HTTP Version Not Supported");
	}
	
	public void read() {
		readFetch();
		readVisit();
		readTotal();
	}
	
	private void readFetch() {
		BufferedReader br = null;
		try {
			String line = "";
			br = new BufferedReader(new FileReader(FETCH));
			while ((line = br.readLine()) != null) {
				line = line.trim();
				String[] tokens = line.split(",");
				if (tokens.length != 2) {
					continue;
				} else {
					if (statusCodes.containsKey(tokens[1])) {
						statusCodes.put(tokens[1], statusCodes.get(tokens[1]) + 1);
					} else {
						statusCodes.put(tokens[1], 1);
					}
					fetch_stat.put("fetches attempted", fetch_stat.get("fetches attempted") + 1);
					if (tokens[1].charAt(0) == '2') {
						fetch_stat.put("fetches succeeded", fetch_stat.get("fetches succeeded") + 1);
					} else {
						if (tokens[1].equals("408")) {
							fetch_stat.put("fetches aborted", fetch_stat.get("fetches aborted") + 1);
						} else {
							fetch_stat.put("fetches failed", fetch_stat.get("fetches failed") + 1);
						}
					}
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readVisit() {
		BufferedReader br = null;
		try {
			String line = "";
			br = new BufferedReader(new FileReader(VISIT));
			while ((line = br.readLine()) != null) {
				line = line.trim();
				String[] tokens = line.split(",");
				if (tokens.length != 4) {
					continue;
				} else {
					totalExtracted += getNumber(tokens[2]);
					putFileSize(tokens[1]);
					if (contentType.containsKey(tokens[3])) {
						contentType.put(tokens[3], contentType.get(tokens[3]) + 1);
					} else {
						contentType.put(tokens[3], 1);
					}
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int getNumber(String str) {
		try {
			int num = Integer.parseInt(str);
			return num;
		} catch (Exception e) {
			return 0;
		}
	}
	
	private void putFileSize(String str) {
		int size = getNumber(str);
		if (size < 1024) {
			fileSize.put("< 1KB", fileSize.get("< 1KB") + 1);
		} else if (size >= 1024 && size < 10240) {
			fileSize.put("1KB ~ <10KB", fileSize.get("1KB ~ <10KB") + 1);
		} else if (size >= 10240 && size < 102400) {
			fileSize.put("10KB ~ <100KB", fileSize.get("10KB ~ <100KB") + 1);
		} else if (size >= 102400 && size < 1024 * 1024) {
			fileSize.put("100KB ~ <1MB", fileSize.get("100KB ~ <1MB") + 1);
		} else {
			fileSize.put(">= 1MB", fileSize.get(">= 1MB") + 1);
		}
	}
	
	private void readTotal() {
		BufferedReader br = null;
		try {
			String line = "";
			br = new BufferedReader(new FileReader(TOTAL));
			while ((line = br.readLine()) != null) {
				String[] tokens = line.trim().split(",");
				if (tokens.length < 2) {
					continue;
				} else {
					if (uniqueURL.contains(tokens[0])) {
						continue;
					} else {
						uniqueURL.add(tokens[0]);
						if (tokens[1].equals("OK")) {
							uniqueURL_in++;
						} else {
							uniqueURL_out++;
						}
					}
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write() {
		FileWriter fr = null;
		try {
			fr = new FileWriter(STAT);
			fr.write("Name: Chengyu Xin\n");
			fr.write("USC ID: 4499177458\n");
			fr.write("News site crawled: http://www.nytimes.com/\n");
			fr.write("\n");
			fr.write("\n");
			fr.write("Fetch Statistics:\n");
			fr.write("================\n");
			fr.write("# fetches attempted: " + fetch_stat.get("fetches attempted") + "\n");
			fr.write("# fetches succeeded: " + fetch_stat.get("fetches succeeded") + "\n");
			fr.write("# fetches aborted: " + fetch_stat.get("fetches aborted") + "\n");
			fr.write("# fetches failed: " + fetch_stat.get("fetches failed") + "\n");
			fr.write("\n");
			fr.write("\n");
			fr.write("Outgoing URLs:\n");
			fr.write("==============\n");
			fr.write("Total URLs extracted: " + totalExtracted + "\n");
			fr.write("# unique URLs extracted: " + uniqueURL.size() + "\n");
			fr.write("# unique URLs within News Site: " + uniqueURL_in + "\n");
			fr.write("# unique URLs outside News Site: " + uniqueURL_out + "\n");
			fr.write("\n");
			fr.write("\n");
			fr.write("Status Codes:\n");
			fr.write("=============\n");
			for (Map.Entry<String, Integer> entry : statusCodes.entrySet()) {
				String line = entry.getKey();
				line = line + " " + statusDetails.get(line) + ": " + entry.getValue() + "\n";
				fr.write(line);
			}
			fr.write("\n");
			fr.write("\n");
			fr.write("File Sizes:\n");
			fr.write("===========\n");
			fr.write("< 1KB" + ": " + fileSize.get("< 1KB") + "\n");
			fr.write("1KB ~ <10KB" + ": " + fileSize.get("1KB ~ <10KB") + "\n");
			fr.write("10KB ~ <100KB" + ": " + fileSize.get("10KB ~ <100KB") + "\n");
			fr.write("100KB ~ <1MB" + ": " + fileSize.get("100KB ~ <1MB") + "\n");
			fr.write(">= 1MB" + ": " + fileSize.get(">= 1MB") + "\n");
			fr.write("\n");
			fr.write("\n");
			fr.write("Content Types:\n");
			fr.write("==============\n");
			for (Map.Entry<String, Integer> entry : contentType.entrySet()) {
				String line = entry.getKey();
				line = line + ": " + entry.getValue() + "\n";
				fr.write(line);
			}
			fr.flush();
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyFileReader fr = new MyFileReader();
		fr.read();
		fr.write();
	}

}
