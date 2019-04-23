package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;


public class CrawlThemefinderMozartHaydn {

	public static void crawl(String urlString, String localFilePath) {
		try {
			System.out.println("Copying "+urlString+" to "+localFilePath);
			InputStream input = new URL(urlString).openStream();
			byte[] bytes = IOUtils.toByteArray(input);
			input.close();
			OutputStream output = new FileOutputStream(localFilePath);
			IOUtils.write(bytes,output);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		try {
			String[] fileNameFilePaths = {
					"/Users/dave/Documents/Work/Research/Data/Mozart-Haydn/Themefinder/mozart.txt",
					"/Users/dave/Documents/Work/Research/Data/Mozart-Haydn/Themefinder/haydn.txt"
			};
			
			String[] outputFolderPaths = {
					"/Users/dave/Documents/Work/Research/Data/Mozart-Haydn/Themefinder/Mozart/",
					"/Users/dave/Documents/Work/Research/Data/Mozart-Haydn/Themefinder/Haydn/"
			};
			
			String[] composerStrings = {"mozart","haydn"};
			
			for(int i = 0; i < 2; i++) {
				String fileNameFilePath = fileNameFilePaths[i];
				ArrayList<String> fileNames = new ArrayList<String>();
				BufferedReader br = new BufferedReader(new FileReader(fileNameFilePath));
				String l;
				while((l = br.readLine()) != null)
					fileNames.add(l.substring(0,l.indexOf(' ')).trim());
				br.close();

				String outputFolderPath = outputFolderPaths[i];
				String composerString = composerStrings[i];
				for(String fileName : fileNames) {
					crawl("http://kern.ccarh.org/cgi-bin/kern/ksdata?file="+fileName+".krn&location=/musedata/"+composerString+"/quartet&format=midi", 
							outputFolderPath+fileName+".mid");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
