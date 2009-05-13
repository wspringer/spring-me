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

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import me.springframework.sample.api.Movie;
import me.springframework.sample.api.MovieFinder;
import me.springframework.sample.javame.factory.BeanFactory;


public class MovieMidlet extends MIDlet {

    private BeanFactory factory = new BeanFactory();

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
    }

    protected void pauseApp() {
    }

    protected void startApp() throws MIDletStateChangeException {
        MovieFinder finder = (MovieFinder) factory.getBean("movieFinder");
        Vector movies = finder.findAll();
        Display.getDisplay(this).setCurrent(createMoviePanel(movies));
    }

    private final Displayable createMoviePanel(Vector movies) {
        List displayable = new List("Movies", List.IMPLICIT);
        Enumeration enumeration = movies.elements();
        while (enumeration.hasMoreElements()) {
            Movie movie = (Movie) enumeration.nextElement();
            displayable.append(movie.getTitle() + " (" + movie.getYear() + ")", null);
        }
        return displayable;
    }

}
