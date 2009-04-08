package me.springframework.sample.javame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.springframework.codec.Codec;
import me.springframework.codec.CodecException;
import me.springframework.javame.rms.IOUtils;
import me.springframework.sample.api.BaseMovie;
import me.springframework.sample.api.Movie;

public class MovieCodec implements Codec {

    public Object decode(byte[] input) throws CodecException {
        DataInputStream in = null;
        try {
            in = new DataInputStream(new ByteArrayInputStream(input));
            BaseMovie movie = new BaseMovie();
            movie.setTitle(in.readUTF());
            movie.setDirector(in.readUTF());
            movie.setYear(in.readInt());
            return movie;
        } catch(IOException ioe) {
            throw new CodecException(ioe.getMessage());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public byte[] encode(Object object) throws CodecException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(result);
            Movie movie = (Movie) object;
            out.writeUTF(movie.getTitle());
            out.writeUTF(movie.getDirector());
            out.writeInt(movie.getYear());
        } catch(IOException ioe) {
            throw new CodecException(ioe.getMessage());
        } finally {
            IOUtils.closeQuietly(out);
        }
        return result.toByteArray();
    }

}
