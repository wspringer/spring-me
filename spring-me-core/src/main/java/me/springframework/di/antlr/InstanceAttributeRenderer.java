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

import me.springframework.di.gen.DotGenerator.StyleCatalog;

import org.antlr.stringtemplate.AttributeRenderer;


/**
 * Renders instances in a particular way.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class InstanceAttributeRenderer implements AttributeRenderer {

    /**
     * The {@link StyleCatalog} to use.
     */
    private StyleCatalog catalog;

    /**
     * Constructs a new instance, accepting the {@link StyleCatalog} used to
     * determine what should be rendered in what way.
     * 
     * @param catalog
     *            The {@link StyleCatalog}.
     */
    public InstanceAttributeRenderer(StyleCatalog catalog) {
        this.catalog = catalog;
    }

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
     * {@inheritDoc}
     * 
     * <dl>
     * <dt>"type"</dt>
     * <dd>Returns the simple class name of the instance passed in.</dd>
     * <dt>"color"</dt>
     * <dd>Returns the color to be used to render the instance. (See
     * {@link StyleCatalog#getColor(String)}.)</dd>
     * </dl>
     */
    public String toString(Object obj, String formatName) {
        if ("type".equals(formatName)) {
            return getSimpleTypeName((String) obj);
        } else if ("color".equals(formatName)) {
            return catalog.getColor(obj.toString());
        } else {
            return toString(obj);
        }
    }

    /**
     * Returns the simple class name of the type passed in, in order to prevent
     * the diagram from being polluted with useless package information.
     * 
     * @param type
     *            The type that needs to be rendered in a simplified way.
     * @return A simplified representation of the type passed in.
     */
    private String getSimpleTypeName(String type) {
        int i = type.lastIndexOf('.');
        if (i >= 0) {
            return type.substring(i + 1);
        } else {
            return type;
        }
    }

}
