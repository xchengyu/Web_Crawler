import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;



public class MyCrawler extends WebCrawler {
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String FETCH = "data/fetch_NewsSite.csv";
	private static final String VISIT = "data/visit_NewsSite.csv";
	private static final String TOTAL = "data/urls_NewsSite.csv";
	private static FileWriter fileWriter_FETCH = null;
	private static FileWriter fileWriter_VISIT = null;
	private static FileWriter fileWriter_TOTAL = null;
	@Override
	public void onBeforeExit() {
		// TODO Auto-generated method stub
		if (fileWriter_FETCH != null) {
			try {
				fileWriter_FETCH.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (fileWriter_VISIT != null) {
			try {
				fileWriter_VISIT.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (fileWriter_TOTAL != null) {
			try {
				fileWriter_TOTAL.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onBeforeExit();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		try {
			if (fileWriter_FETCH == null) {
				fileWriter_FETCH = new FileWriter(FETCH);
			}
			if (fileWriter_VISIT == null) {
				fileWriter_VISIT = new FileWriter(VISIT);
			}
			if (fileWriter_TOTAL == null) {
				fileWriter_TOTAL = new FileWriter(TOTAL);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onStart();
	}

	private final static Pattern FILTERS_TEXT = Pattern.compile(".*(\\.(html|pdf|doc))$");
	private final static Pattern FILTERS_IMAGE = Pattern.compile(".*(\\.(gif|jpeg|png))$");
	private final static String root_http = "http://www.nytimes.com/";
	private final static String root_https = "https://www.nytimes.com/";
	/**
	* This method receives two parameters. The first parameter is the page
	* in which we have discovered this new url and the second parameter is
	* the new url. You should implement this function to specify whether
	* the given url should be crawled or not (based on your crawling logic).
	* In this example, we are instructing the crawler to ignore urls that
	* have css, js, git, ... extensions and to only accept urls that start
	* with "http://www.viterbi.usc.edu/". In this case, we didn't need the
	* referringPage parameter to make the decision.
	*/
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase().trim();
		try {
			String content = "";
			if (href.startsWith(root_http) || href.startsWith(root_https)) {
				content = href + COMMA_DELIMITER + "OK" + NEW_LINE_SEPARATOR;
			} else {
				content = href + COMMA_DELIMITER + "N_OK" + NEW_LINE_SEPARATOR; 
			}
			fileWriter_TOTAL.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isValid(href);
	}
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode,
			String statusDescription) {
		// TODO Auto-generated method stub
		String href = webUrl.getURL().toLowerCase().trim();
		if (!href.startsWith(root_http) && !href.startsWith(root_https)) {
			 href = root_http + href;
		 }
		String details = href + COMMA_DELIMITER + statusCode + NEW_LINE_SEPARATOR;
		try {
			fileWriter_FETCH.write(details);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.handlePageStatusCode(webUrl, statusCode, statusDescription);
	}

	private boolean isValid(String href) {
		if (href == null || href.length() == 0) {
			return false;
		}
		if (href.equals(root_http) || href.equals(root_https)) {
			return true;
		}
		if (href.startsWith(root_http) || href.startsWith(root_https)) {
			if (FILTERS_TEXT.matcher(href).matches() || FILTERS_IMAGE.matcher(href).matches()) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	 @Override
	 public void visit(Page page) {
		 String url = page.getWebURL().getURL().trim();
//		 System.out.println("URL: " + url);
		 int linkSize = 0;
		 if (page.getParseData() instanceof HtmlParseData) {
			 HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//			 String text = htmlParseData.getText();
//			 String html = htmlParseData.getHtml();
			 Set<WebURL> links = htmlParseData.getOutgoingUrls();
//			 System.out.println("Status: " + page.getStatusCode());
//			 System.out.println("Text length: " + text.length());
//			 System.out.println("Html length: " + html.length());
//			 System.out.println("Number of outgoing links: " + links.size());
			 linkSize = links.size();
		 }
		 if (url == null || url.length() == 0) {
			 url = root_https;
		 }
		 if (!url.startsWith(root_http) && !url.startsWith(root_https)) {
			 url = root_http + url;
		 }
		 int pageSize = 0;
		 try {
			 if (page.getStatusCode() < 300 && page.getStatusCode() >= 200) {
				 pageSize = page.getContentData().length;
				 String details = url + COMMA_DELIMITER + pageSize 
						 + COMMA_DELIMITER + linkSize + 
						 COMMA_DELIMITER;
				 String type = page.getContentType() == null ? "text/html" : page.getContentType();
				 if (type.startsWith("text/html")) {
					 type = "text/html";
				 }
				 details += type + NEW_LINE_SEPARATOR;
				 fileWriter_VISIT.write(details);
			 }
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	 }
}