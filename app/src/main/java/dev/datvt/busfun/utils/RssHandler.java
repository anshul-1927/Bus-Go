package dev.datvt.busfun.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import dev.datvt.busfun.models.RssItem;


public class RssHandler extends DefaultHandler {

    public static final String ITEM = "item";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LINK = "link";
    public static final String DATE = "pubdate";

    private RssItem items;
    private List<RssItem> itemList = new ArrayList<RssItem>();
    private boolean started = false;
    private StringBuilder sBuilder = new StringBuilder();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (localName.equalsIgnoreCase(ITEM)) {
            items = new RssItem();
            started = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
        if (localName.equalsIgnoreCase(ITEM)) {
            itemList.add(items);
        } else if (started) {
            if (localName.equalsIgnoreCase(TITLE)) {
                items.setTitle(sBuilder.toString().trim());
            } else if (localName.equalsIgnoreCase(DESCRIPTION)) {
                items.setDescription(sBuilder.toString().trim());
            } else if (localName.equalsIgnoreCase(LINK)) {
                items.setLink(sBuilder.toString().trim());
            } else if (localName.equalsIgnoreCase(DATE)) {
                items.setDate(sBuilder.toString().trim());
            }
            sBuilder = new StringBuilder();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        if (started && (sBuilder != null)) {
            sBuilder.append(ch, start, length);
        }
    }

    public List<RssItem> getItemList() {
        return itemList;
    }

}
