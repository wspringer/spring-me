/**
 * Copyright (C) 2009 Original Authors
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Spring ME; see the file COPYING. If not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module. An independent module is a module which is not derived from or
 * based on this library. If you modify this library, you may extend this
 * exception to your version of the library, but you are not obligated to
 * do so. If you do not wish to do so, delete this exception statement
 * from your version.
 */
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
