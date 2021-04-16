package runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import crawler.Crawler;

public class CrawlerRunner {

	public static void main(String[] args) throws IOException, InterruptedException {

		Crawler crawler = new Crawler();

		System.out.println("\n\n\nStarting the crawler ...... \n\n\n");
		
		crawler.search("https://www.crawler-test.com/", "test");

		System.out.println("\n\n\nCrawler finished its job, please check ouput.txt for results ~  \n\n\n");
	
	}

}
