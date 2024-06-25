package com.example.savushkin_practice_no2.Data.Util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

class XMLParser {
    static int index;
    static String title = null;
    public static ArrayList<Element> elements = new ArrayList<>();

    static XMLObject start(InputStream xml) throws ParserConfigurationException, SAXException, IOException {
        elements.clear();

        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser saxParser = spf.newSAXParser();
        MyHandler handler = new MyHandler();
        saxParser.parse(xml, handler);
        XMLObject xmlObject = handler.getDeliveryNote();
        //String tag = xmlObject.getTag();
        //String value = xmlObject.getValue();
        return xmlObject;
    }

    static class MyHandler extends DefaultHandler {
        private XMLObject main = null;
        private String currentTag = null;
        private XMLObject inside = null;
        private ArrayList<Element> locE = new ArrayList<>();
        XMLObject getDeliveryNote() {
            return main;
        }

        @Override
        public void startElement(
                String uri,
                String localName,
                String qName,
                Attributes attributes
        ) {
            title = qName;
            locE.add(new Element(title));
            index = locE.size()-1; //Он возвращает -1, если указанный элемент не существует в списке.




            if (main == null) {
                main = new XMLObject(qName);
                if (attributes != null) {
                    for (int i = 0; i < attributes.getLength(); i++) {
                        main.addAttribute(attributes.getQName(i), attributes.getValue(i));
                    }
                }
                inside = main;
            } else {
                XMLObject xmlObject = new XMLObject(qName);
                xmlObject.setParent(inside);
                inside.add(xmlObject);
                if (attributes != null) {
                    for (int i = 0; i < attributes.getLength(); i++) {
                        xmlObject.addAttribute(attributes.getQName(i), attributes.getValue(i));
                    }
                }
                currentTag = qName;
                inside = xmlObject;
            }
        }


        @Override
        public void endElement(String uri, String localName, String qName) {
            if (inside != main)
                inside = inside.getParent();


            index = locE.size()-1;
            if(index>0) {
//                locE.get(index-1).innerElements = new Element(locE.get(index));
                locE.get(index - 1).innerElements.add(new Element(locE.get(index)));
                locE.remove(index);
            }
            else
                elements.add(new Element(locE.get(0)));

        }


        @Override
        public void characters(char[] ch, int start, int length) {
            String text = new String(ch, start, length);
            if (text.contains("<") || currentTag == null || text.contains("\t\t") || text.contains(
                    "\n") || text.contains("\t")
            ) return;
            XMLObject objectXML = inside.find(currentTag);
            objectXML.setValue(text);
            locE.get(locE.size()-1).value = text;
        }
    }
}
