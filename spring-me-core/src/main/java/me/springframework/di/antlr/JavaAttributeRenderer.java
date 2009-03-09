/**
 * Copyright (C) 2008 TomTom
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Spring ME; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
package me.springframework.di.antlr;

import org.antlr.stringtemplate.AttributeRenderer;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * An {@link AttributeRenderer} capable of turning String values in a certain
 * representation.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class JavaAttributeRenderer implements AttributeRenderer {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.antlr.stringtemplate.AttributeRenderer#toString(java.lang.Object)
     */
    public String toString(Object obj) {
        return obj.toString();
    }

    /**
     * {@inheritDoc} Supports two different format names:
     * 
     * <dl>
     * <dt>"className"</dt>
     * <dd>Turn the String into a camel-case formatted String, starting with an
     * uppercase character.
     * <dt>"constant"</dt>
     * <dd>Turn the String into an all uppercase, underscore-separated String.</dd>
     * <dt>"escapeJava"</dt>
     * <dd>Escape all characters that should be escaped in a literal representation of the String passed in.</dd>
     * </dl>
     */
    public String toString(Object obj, String formatName) {
        if ("className".equals(formatName)) {
            return FormattingUtils.toCamelCase(obj.toString(), true);
        } else if ("constant".equals(formatName)) {
            return FormattingUtils.toUpperCase(obj.toString());
        } else if ("escapeJava".equals(formatName)) {
            return StringEscapeUtils.escapeJava(obj.toString());
        } else {
            return FormattingUtils.toCamelCase(obj.toString(), false);
        }
    }

}
