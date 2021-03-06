package org.hivedb.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Compresses and decompresses between XML and a GZipped InputStream
 * @author alikuski@cafepress.com
 *
 */
public class Compression {
	public static InputStream compress(String xml) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes());
	    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gZipOutputStream;
		try {
			gZipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	   
	    byte[] buffer = new byte[1024];
	    try {
	    	int size;
			while ((size = byteArrayInputStream.read(buffer)) != -1) {
				gZipOutputStream.write(buffer, 0, size);
			}
			gZipOutputStream.finish();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	    return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	}
	
	public static String decompress(InputStream inputStream) {
		
	    GZIPInputStream gZipInputStream;
		try {
			gZipInputStream = new GZIPInputStream(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		// Use an input stream reader to convert the bytes to chars
	    InputStreamReader inputStreamReader = new InputStreamReader(gZipInputStream);
	    StringBuffer stringBuffer = new StringBuffer();
	    char[] charBuffer = new char[1024];
	    try {
	    	int size;
			while ((size = inputStreamReader.read(charBuffer)) != -1) {
			  stringBuffer.append(charBuffer, 0, size);
			}
		} catch (IOException e) {
			new RuntimeException(e);
		}
	    return stringBuffer.toString();
	}
}
