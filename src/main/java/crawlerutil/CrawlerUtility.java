package crawlerutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.text.html.parser.DTD;
import org.apache.commons.io.FileUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerUtility {

	private List<String> links = new LinkedList<String>();
	private Document htmlDocument;
	private final static String USER_AGENT = "BOT";

	public boolean isAllowed(String url) {

		String cmd = System.getProperty("user.dir") + "/robotstxt/c-build/robots" + " " + System.getProperty("user.dir")
				+ "/robots.txt " + USER_AGENT + " " + url;
		Scanner sc = new Scanner(System.in);

		try {

			Process process = Runtime.getRuntime().exec(cmd);
			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			process.waitFor();
			if (output.toString().contains("DISALLOWED")) {
				System.err.println("Cannot read robots.txt");
				System.out.println("Do you still want to continue crawling ? \n Enter 'Yes' or 'No'");
				String c = sc.nextLine();

				if (c.equals("Yes")) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
		return false;
	}

	public void crawlUrl(String url) {

		try {
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);

			if (!isAllowed(url)) {
				System.out.println("THIS URL IS NOT ALLOWED TO BE CRAWLED ...");
				return;
			}

			Document htmlDoc = connection.get();
			this.htmlDocument = htmlDoc;

			if (connection.response().statusCode() == 200) {
				System.out.println("Visiting.. " + url);
			} else {
				return;
			}
			if (!connection.response().contentType().contains("text/html")) {
				System.err.println("Failure content was not TEXT/HTML");
				return;
			}
			Elements linksWithinPage = htmlDocument.select("a[href]");
			System.out.println("FOUND " + linksWithinPage.size() + " links on " + url + "\n\n\n");
			for (Element eachLink : linksWithinPage) {
				this.links.add(eachLink.absUrl("href"));
			}
			return;

		} catch (IOException e) {
			System.err.println(e.getMessage());
			return;
		}
	}

	public boolean doesWordExist(String word) {

		if (this.htmlDocument == null) {
			System.err.println("ERROR! Call crawl() before performing analysis on the document");
			return false;
		}
		String body = this.htmlDocument.body().text();
		StringBuilder stringBuilder = new StringBuilder();
		Elements allRef = htmlDocument.select("a");

		for (Element element : allRef) {
			stringBuilder.append(element.text() + "\n");
		}

		String fulltext = body + stringBuilder.toString();
		return fulltext.toLowerCase().contains(word.toLowerCase());
	}

	public List<String> getReferredLinks() {

		return this.links;
	}

	// public List<String> findAllwords(String url) {

	// if (htmlDocument == null) {
	// if (this.htmlDocument == null) {
	// System.err.println("ERROR! Call crawl() before performing analysis on the
	// document");
	// return Collections.emptyList();
	// }
	// }

	// System.out.println("Fetching all words, Please wait ......");

	// StringBuilder stringBuilder = new StringBuilder();
	// Elements allRef = htmlDocument.select("a");

	// for (Element element : allRef) {
	// stringBuilder.append(element.text() + "\n");
	// }
	// String bodyText = this.htmlDocument.body().text();
	// String[] text = stringBuilder.toString().split("\\s+");
	// String[] wordArray = bodyText.split("\\s+");

	// Set<String> uniqueWords = new HashSet<>();

	// for (String e : wordArray) {
	// uniqueWords.add(e);
	// }
	// for (String e : text) {
	// uniqueWords.add(e);
	// }
	// List<String> uniqueWordsList = new ArrayList<>();

	// for (String e : uniqueWords) {
	// uniqueWordsList.add(e + "------------------------------> " + url + "\n");
	// }
	// return uniqueWordsList;
	// }

}