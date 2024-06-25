package com.example.savushkin_practice_no2.Data.Util;

import static com.example.savushkin_practice_no2.Data.Util.XMLParser.elements;

import android.content.ContentValues;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class Java {

    public List<ContentValues> parce(InputStream xml) {
        XMLObject XMLObject = null;
        try {
            XMLObject = XMLParser.start(xml);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        ArrayList<Element> dataEl = new ArrayList<>(elements);

        List<ContentValues> dataList = new ArrayList<>();
        ContentValues contentValues = new ContentValues();

        dataList = printElements(dataEl, 0, contentValues, dataList);
        dataList.add(new ContentValues(contentValues));

        return dataList;
    }


    public List<ContentValues> printElements(ArrayList<Element> elements, int indent,ContentValues contentValues, List<ContentValues> dataList ) {


        for (Element element : elements) {
            if (element.innerElements != null && !element.innerElements.isEmpty()) {
                if (contentValues.size() != 0) {
                    dataList.add(new ContentValues(contentValues));
                    contentValues.clear();
                }
                printElements(element.innerElements, indent + 2,contentValues, dataList);
            }if (element.innerElements == null || element.innerElements.isEmpty()){
                contentValues.put(element.title, element.value);
            }
        }
        return  dataList;
    }
}
