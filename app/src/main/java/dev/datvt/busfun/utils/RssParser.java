package dev.datvt.busfun.utils;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dev.datvt.busfun.models.RssItem;


public class RssParser {
    public List<RssItem> getNewsList(String link) {
        try {
            URL url = new URL(link);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            RssHandler hander = new RssHandler();
            reader.setContentHandler(hander);
            InputSource source = new InputSource(url.openStream());
            reader.parse(source);
            return hander.getItemList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
