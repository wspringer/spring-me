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
package me.springframework.di.spring;

public class Bean1 {

    private int dummy;
    private String dummy2;
    private Bean2 bean2;
    private Bean3 bean3;

    public Bean1() {
    }

    public Bean1(final int dummy, final String dummy2, final Bean2 bean2, final Bean3 bean3) {
        this.dummy = dummy;
        this.dummy2 = dummy2;
        this.bean2 = bean2;
        this.bean3 = bean3;
    }

    public void setDummy(final int dummy) {
        this.dummy = dummy;
    }

    public int getDummy() {
        return dummy;
    }

    public void setDummy2(final String dummy2) {
        this.dummy2 = dummy2;
    }

    public String getDummy2() {
        return dummy2;
    }

    public Bean2 getBean2() {
        return bean2;
    }

    public void setBean2(final Bean2 bean2) {
        this.bean2 = bean2;
    }

    public Bean3 getBean3() {
        return bean3;
    }

    public void setBean3(final Bean3 bean3) {
        this.bean3 = bean3;
    }

}
