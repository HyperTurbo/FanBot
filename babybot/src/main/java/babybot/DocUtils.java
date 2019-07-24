package babybot;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *  
 */

class DocUtils {
	
	static String getShips(Document doc) {
		String ships = "";

		try {
			Elements contents = doc.getElementsByClass("relationship tags");
			for(Element content : contents) {
				int index = 1;
				Elements relationshipTags = content.getElementsByClass("tag");
				for(Element tag : relationshipTags) {
					if(!(index == 1) || index == relationshipTags.size()) {
						ships+=", ";
					}
					ships+=tag.text();
					index++;

				}
			}
			if(ships.isEmpty()) {
				ships = "None";
			}
		} catch (Exception e) {
			//
		}
		return ships;
	}
	
	static String getTitle(Document doc) {
		String title = "";
		try {
			Elements contents = doc.getElementsByClass("title heading");
			for(Element content : contents) {
				title = content.text();
			}
			if(title.isEmpty()) {
				title = "None";
			}
		} catch (Exception e) {
			//
		}
		return title;
	}

	public static String getAuthor(Document doc) {
		String author = "";
		try {
			Elements contents = doc.getElementsByClass("byline heading");
			for(Element content : contents) {
				author = content.text();
			}
			if(author.isEmpty()) {
				author = "None";
			}
		} catch (Exception e) {
			//
		}
		return author;
	}
	
	public static String getSummary(Document doc) {
		String author = "";
		try {
			Elements contents = doc.getElementsByClass("userstuff");
			for(Element content : contents) {
				author = content.text();
			}
			if(author.isEmpty()) {
				author = "None";
			}
		} catch (Exception e) {
			//
		}
		return author;
	}

	public static String getHiddenLink(String text, String url) {
		return "["+text+"]("+url+")";
	}
}
