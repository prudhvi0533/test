package com.freevideotuts.wget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Wget {
	static Map<String,List<String>> totalfiles = new LinkedHashMap<>();
	
	private void parseAllFoldersAndContents(String url) {
		List<String> files = new ArrayList<>();
		
		try {
		    Document doc = Jsoup.connect(url).get();
	 	    Elements links = doc.select("td a");
	 	   
	 	    for (Element link : links) {
	 	        if(link.text().equals("Parent Directory"))
	 	        	continue;
	 	        if(link.attr("href").endsWith("/")) {
	 	        	parseAllFoldersAndContents(url+link.attr("href"));
	 	        }else {
	 	        	files.add(url+link.attr("href"));
	 	        }
	 	    }
	 	   totalfiles.put(url, files);
		} catch (IOException ex) {
    	    ex.printStackTrace();
    	}
	}
	
	public static void downloadFile(final String url, final File folder) {
		
		try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
				  FileOutputStream fileOutputStream = new FileOutputStream(folder)) {
				    byte dataBuffer[] = new byte[1024];
				    int bytesRead;
				    while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				        fileOutputStream.write(dataBuffer, 0, bytesRead);
				    }
				} catch (IOException e) {
				   e.printStackTrace();
				}
	}

    public static void main(String[] args) {
    	try {
    		String mainUrl="http://tr-1.myfast.link/torrents/%5bFreeCoursesOnline.Me%5d%20MASTERCLASS%20-%20Phil%20IveyTeaches%20Poker%20Strategy/";
    		String target="E:\\MASTERCLASS - Phil IveyTeaches Poker Strategy\\";
    		
    		Wget wget = new Wget();
    		wget.parseAllFoldersAndContents(mainUrl);
    		
			totalfiles.forEach((k, v) -> {
				String folder= k.replace(mainUrl, "");				
				File targetFolder =null;
				if(!folder.isEmpty()) {
					 try {
						 targetFolder=new File(URLDecoder.decode(target+folder, StandardCharsets.UTF_8.toString()));
						 targetFolder.mkdir();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(v.size()>0) {
					for (Iterator<String> iterator = v.iterator(); iterator.hasNext();) {
						String file =  iterator.next();
						String filename=file.replace(k, "");
						try {
							downloadFile(file,new File(URLDecoder.decode(target+folder+filename, StandardCharsets.UTF_8.toString())));
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
				}
				
				
				
			});
    	   
    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	}
    }
}
