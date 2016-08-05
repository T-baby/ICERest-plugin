package com.cybermkd.plugin.redis.serializer;

import com.cybermkd.log.Logger;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import redis.clients.util.SafeEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * FstSerializer.
 */
public class FstSerializer implements ISerializer {
	
	public static final ISerializer me = new FstSerializer();
	
	public byte[] keyToBytes(String key) {
		return SafeEncoder.encode(key);
	}
	
	public String keyFromBytes(byte[] bytes) {
		return SafeEncoder.encode(bytes);
	}
	
	public byte[] fieldToBytes(Object field) {
		return valueToBytes(field);
	}
	
    public Object fieldFromBytes(byte[] bytes) {
    	return valueFromBytes(bytes);
    }

	private static Logger logger=Logger.getLogger(FstSerializer.class);
	
	public byte[] valueToBytes(Object value) {
		FSTObjectOutput fstOut = null;
		try {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			fstOut = new FSTObjectOutput(bytesOut);
			fstOut.writeObject(value);
			fstOut.flush();
			return bytesOut.toByteArray();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if(fstOut != null)
				try {fstOut.close();} catch (IOException e) {logger.error(e.getMessage(), e);}
		}
	}
	
	public Object valueFromBytes(byte[] bytes) {
		if(bytes == null || bytes.length == 0)
			return null;
		
		FSTObjectInput fstInput = null;
		try {
			fstInput = new FSTObjectInput(new ByteArrayInputStream(bytes));
			return fstInput.readObject();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if(fstInput != null)
				try {fstInput.close();} catch (IOException e) {logger.error(e.getMessage(), e);}
		}
	}
}



