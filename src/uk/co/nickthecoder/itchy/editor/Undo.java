/*******************************************************************************
 * Copyright (c) 2013 Nick Robinson All rights reserved. This program and the accompanying materials are made available under the terms of
 * the GNU Public License v3.0 which accompanies this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package uk.co.nickthecoder.itchy.editor;

public interface Undo
{
    public void undo();

    public void redo();

    /**
     * Merge two Undo objects together if possible. This Undo is the one that changes, 'other' is left unchanged.
     * 
     * @param other
     *        The Undo, which is to be merged with this one. Other happened AFTER this.
     * @return True if the merge happened.
     */
    public boolean merge( Undo other );
}
