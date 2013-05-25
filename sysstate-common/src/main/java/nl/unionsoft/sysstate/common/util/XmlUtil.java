package nl.unionsoft.sysstate.common.util;

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtil {
    private static final Logger LOG = LoggerFactory.getLogger(XmlUtil.class);

    public static Element getElementWithKeyFromElement(final Element element, final String key) {
        final NodeList nodeList = element.getElementsByTagName(key);
        Element result = null;
        if (nodeList.getLength() > 0) {
            result = (Element) nodeList.item(0);
        }
        return result;
    }

    public static Element getElementWithKeyFromObject(final Object xmlObject, final String key) {
        Element result = null;
        if (xmlObject instanceof Document) {
            result = getElementWithKeyFromDocument((Document) xmlObject, key);
        } else if (xmlObject instanceof Element) {
            result = getElementWithKeyFromElement((Element) xmlObject, key);
        }
        return result;
    }

    public static Element getElementWithKeyFromDocument(final Document document, final String key) {
        final NodeList nodeList = document.getElementsByTagName(key);
        Element result = null;
        if (nodeList.getLength() > 0) {
            result = (Element) nodeList.item(0);
        }
        return result;
    }

    public static String getCharacterDataFromElementWithKey(final Element element, final String key) {
        String result = null;
        if (element != null) {
            final NodeList jobNodeList = element.getElementsByTagName(key);

            final Node node = jobNodeList.item(0);
            if (node != null) {
                final CharacterData characterData = (CharacterData) jobNodeList.item(0).getChildNodes().item(0);
                if (characterData != null) {
                    result = characterData.getNodeValue();
                }
            }
        }
        return result;
    }

    public static String getCharacterDataFromDocumentWithKey(final Document document, final String key) {
        String result = null;
        if (document != null) {
            final NodeList jobNodeList = document.getElementsByTagName(key);
            final CharacterData characterData = (CharacterData) jobNodeList.item(0).getChildNodes().item(0);
            result = characterData.getNodeValue();

        }
        return result;
    }

    public static String getCharacterDataFromObjectWithKey(final Object xmlObject, final String key) {
        String result = null;
        if (xmlObject instanceof Document) {
            result = getCharacterDataFromDocumentWithKey((Document) xmlObject, key);
        } else if (xmlObject instanceof Element) {
            result = getCharacterDataFromElementWithKey((Element) xmlObject, key);
        }
        return result;
    }

    public static String getAttributePropertyFromElement(final Element element, final String attribute) {
        return element.getAttribute(attribute);
    }

    public static XMLGregorianCalendar dateToXml(final Date date) {
        XMLGregorianCalendar result = null;
        if (date != null) {
            result = dateTimeToXml(new DateTime(date));

        }
        return result;
    }

    public static Date xmlToDate(final XMLGregorianCalendar xmlCal) {
        Date result = null;
        if (xmlCal != null) {
            result = xmlToDateTime(xmlCal).toDate();
        }
        return result;
    }

    public static XMLGregorianCalendar dateTimeToXml(final DateTime dateTime) {
        XMLGregorianCalendar xmlGregorianCalendar = null;
        if (dateTime != null) {
            try {
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                xmlGregorianCalendar.setDay(dateTime.getDayOfMonth());
                xmlGregorianCalendar.setMonth(dateTime.getMonthOfYear());
                xmlGregorianCalendar.setYear(dateTime.getYear());
                xmlGregorianCalendar.setHour(dateTime.getHourOfDay());
                xmlGregorianCalendar.setMinute(dateTime.getMinuteOfHour());
                xmlGregorianCalendar.setSecond(dateTime.getSecondOfMinute());
                xmlGregorianCalendar.setMillisecond(dateTime.getMillisOfSecond());
            } catch(final DatatypeConfigurationException e) {
                LOG.error("Unable to dateTimeToXml, caught DatatypeConfigurationException", e);
            }
        }
        return xmlGregorianCalendar;
    }

    public static DateTime xmlToDateTime(final XMLGregorianCalendar xmlCal) {
        DateTime result = null;
        if (xmlCal != null) {
            int hourOfDay = xmlCal.getHour();
            int minuteOfHour = xmlCal.getMinute();
            int secondOfMinute = xmlCal.getSecond();
            int milliOfSecond = xmlCal.getMillisecond();

            if (hourOfDay < 0) {
                hourOfDay = 0;
            }
            if (minuteOfHour < 0) {
                minuteOfHour = 0;
            }
            if (secondOfMinute < 0) {
                secondOfMinute = 0;
            }
            if (milliOfSecond < 0) {
                milliOfSecond = 0;
            }
            result = new DateTime(xmlCal.getYear(), xmlCal.getMonth(), xmlCal.getDay(), hourOfDay, minuteOfHour, secondOfMinute, milliOfSecond);
        }
        return result;
    }
}
