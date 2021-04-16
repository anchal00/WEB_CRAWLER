package crawler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import crawlerutil.CrawlerUtility;
import org.apache.commons.io.FileUtils;

public class Crawler {

	private static final int PAGE_LIMIT = 50;
	private Set<String> visitedPages;
	private List<String> pagesToBeVisited;
	private File outputFile;
	private PrintWriter writer;
	private final CrawlerUtility crawlerUtil;
	private File  robotsTxtFile;

	private Set<String> allWords;

	public Crawler() {

		visitedPages = new HashSet<String>();
		pagesToBeVisited = new LinkedList<String>();
		outputFile = new File(System.getProperty("user.dir") + "/" + "src/main/java/output.txt");
		crawlerUtil = new CrawlerUtility();
		allWords = new HashSet<>();
		try {
			outputFile.createNewFile();
			writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(outputFile)));
		} catch (IOException e) {
			System.err.println("Cannot create output file ");
		}

	}

	private String getNextUrltoCrawl() {

		String nextUrl = null;
		do {
			nextUrl = pagesToBeVisited.remove(0);
		} while (visitedPages.contains(nextUrl));

		visitedPages.add(nextUrl);

		return nextUrl;
	}

	public void search(String url, String searchWord) throws IOException {

		getRobotsTxtFile(url);
		while (visitedPages.size() <= PAGE_LIMIT) {

			String currentUrl = null;

			if (pagesToBeVisited.isEmpty()) {
				currentUrl = url;
				pagesToBeVisited.add(currentUrl);
				visitedPages.add(currentUrl);
			} else {
				currentUrl = getNextUrltoCrawl();

			}
			crawlerUtil.crawlUrl(currentUrl);

			boolean isWordFound = crawlerUtil.doesWordExist(searchWord);

			if (isWordFound) {
				writer.append("The word, " + searchWord + " was found at -------->  " + currentUrl + "\n");
			}
			pagesToBeVisited.addAll(crawlerUtil.getReferredLinks());

		}
		writer.close();
	}

	public void getRobotsTxtFile(String url) throws IOException {
		String robots_Txt_Url = url + "robots.txt";
		robotsTxtFile = new File(System.getProperty("user.dir") + "/" + "robots.txt");
		robotsTxtFile.createNewFile();
		FileUtils.copyURLToFile(new URL(robots_Txt_Url), robotsTxtFile, 2400, 2400);

	}
}
