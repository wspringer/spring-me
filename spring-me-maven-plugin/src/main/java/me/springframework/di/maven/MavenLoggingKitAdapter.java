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
package me.springframework.di.maven;

import org.apache.maven.plugin.logging.Log;

import com.agilejava.blammo.LoggingKitAdapter;
import com.agilejava.blammo.MessageProducer;

/**
 * A {@link LoggingKitAdapter} adapter wrapping around Maven's {@link Log}
 * object.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class MavenLoggingKitAdapter implements LoggingKitAdapter {

    /**
     * The {@link Log} object wrapped.
     */
    private Log log;

    /**
     * Constructs a new instance, acceptin the {@link Log} to wrap.
     * 
     * @param log The {@link Log} to wrap.
     */
    public MavenLoggingKitAdapter(Log log) {
        this.log = log;
    }

    /*
     * (non-Javadoc)
     * @see com.agilejava.blammo.LoggingKitAdapter#log(java.lang.String, com.agilejava.blammo.MessageProducer)
     */
    public void log(String level, MessageProducer producer) {
        if ("info".equals(level) && log.isInfoEnabled()) {
            log.info(producer.getMessage());
        }
        if ("debug".equals(level) && log.isDebugEnabled()) {
            log.debug(producer.getMessage());
        }
        if ("error".equals(level) && log.isErrorEnabled()) {
            log.error(producer.getMessage());
        }
        if ("warn".equals(level) && log.isWarnEnabled()) {
            log.warn(producer.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.agilejava.blammo.LoggingKitAdapter#log(java.lang.String, com.agilejava.blammo.MessageProducer, java.lang.Throwable)
     */
    public void log(String level, MessageProducer producer, Throwable throwable) {
        if ("info".equals(level) && log.isInfoEnabled()) {
            log.info(producer.getMessage(), throwable);
        }
        if ("debug".equals(level) && log.isDebugEnabled()) {
            log.debug(producer.getMessage(), throwable);
        }
        if ("error".equals(level) && log.isErrorEnabled()) {
            log.error(producer.getMessage(), throwable);
        }
        if ("warn".equals(level) && log.isWarnEnabled()) {
            log.warn(producer.getMessage(), throwable);
        }
    }

}
