package me.springframework.codec;


public interface Codec {

    byte[] encode(Object object) throws CodecException;
    
    Object decode(byte[] input) throws CodecException;
    
}
