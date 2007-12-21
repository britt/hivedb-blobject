package org.hivedb.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *  Utility converters
 * @author alikuski@cafepress.com
 *
 */
public class Converters {
	/**
	 * Converts an inputStream to a byte array
	 * @param inputStream
	 * @return
	 */
	public static byte[] getBytes(InputStream inputStream) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while((len = inputStream.read(buffer)) >= 0)
				out.write(buffer, 0, len);
			inputStream.reset();
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out.toByteArray();
	}
}
