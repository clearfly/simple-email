package com.outjected.email.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.google.common.base.Charsets;

public class XMLUtil {

    public static String marshal(Object o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshal(o, bos, new HashMap<>());
        return bos.toString();
    }

    public static String marshal(Object o, Map<String, Object> properties) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshal(o, bos, properties);
        return bos.toString();
    }

    public static void marshal(Object o, OutputStream os) {
        marshal(o, os, new HashMap<>());
    }

    public static void marshal(Object o, OutputStream os, Map<String, Object> properties) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(o.getClass());
            Marshaller marshaller = ctx.createMarshaller();

            for (Entry<String, Object> p : properties.entrySet()) {
                marshaller.setProperty(p.getKey(), p.getValue());
            }

            marshaller.marshal(o, os);
        }
        catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(Class<T> clazz, InputStream is) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller um = jc.createUnmarshaller();
        return (T) um.unmarshal(is);
    }

    public static <T> T unmarshal(Class<T> clazz, String xml) throws JAXBException {
        return unmarshal(clazz, new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8)));
    }
}
