/*
 * ExTextBox.java
 *
 * Created on 18.06.2008, 9:16
 * Copyright (c) 2006-2008, Daniel Apatin (ad), http://apatin.net.ru
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * You can also redistribute and/or modify this program under the
 * terms of the Psi License, specified in the accompanied COPYING
 * file, as published by the Psi Project; either dated January 1st,
 * 2005, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package ui.controls;

import Client.Config;
import Client.StaticData;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import locale.SR;
//#ifdef ARCHIVE
import Archive.ArchiveList;
//#endif
import ui.VirtualCanvas;
import ui.VirtualList;
import org.bombusmod.util.ClipBoardIO;


/**
 *
 * @author ad
 */
public class ExTextBox {
    
    public final TextBox textbox = new TextBox("", "", 500, TextField.ANY);
    protected Displayable parentView = midlet.BombusMod.getInstance().getCurrentDisplayable();
    protected StaticData sd = StaticData.getInstance();
    
    public String body;
    private String subj;
    protected int caretPos;

    protected Config cf;    
    
//#ifdef ARCHIVE
    protected Command cmdArchive = new Command(SR.MS_ARCHIVE, Command.SCREEN, 6);
//#endif
//#if TEMPLATES
    protected Command cmdTemplate = new Command(SR.MS_TEMPLATE, Command.SCREEN, 7);
//#endif  
//#ifdef CLIPBOARD
    private Command cmdCopy = new Command(SR.MS_COPY, Command.SCREEN, 3);
    private Command cmdCopyPlus = new Command("+ "+SR.MS_COPY, Command.SCREEN, 4);    
    protected Command cmdPasteText = new Command(SR.MS_PASTE, Command.SCREEN, 8);
//#endif

    private VirtualList parentList;
    
    int maxSize = 500;
    
    final boolean writespaces;
            
    /** Creates a new instance of UniTextEdit */
    public ExTextBox(VirtualList parent, String body, String subj, boolean writespaces) {
        cf = Config.getInstance();
        textbox.setTitle(subj);
        parentList = parent;
	this.writespaces = writespaces;	
        try {
            //expanding buffer as much as possible
            maxSize = textbox.setMaxSize(4096); //must not trow
            insert(body, 0, writespaces);            
         } catch (Exception e) {}       
        
    }

    public ExTextBox(VirtualList parent, String body, String subj) {
        this(parent, body, subj, true);
    }

    public void show(CommandListener listener) {
        commandState();
        textbox.setCommandListener(listener);                
        midlet.BombusMod.getInstance().setDisplayable(textbox);                
    }
        
    public void destroyView() {                
        midlet.BombusMod.getInstance().setDisplayable(parentView);
        VirtualCanvas.getInstance().show(parentList);
    }   
    
    public final void insert(String s, int caretPos) {
        insert(s, caretPos, true);
    }

    public final void insert(String s, int caretPos, boolean writespaces) {
        if (s == null) return;

        String src = textbox.getString();

        StringBuffer sb = new StringBuffer(s);

        if (writespaces) {
            if (caretPos > 0) {
                if (src.charAt(caretPos - 1) != ' ') {
                    sb.insert(0, ' ');
                }
            }

            if (caretPos < src.length()) {
                if (src.charAt(caretPos) != ' ') {
                    sb.append(' ');
                }
            }

            if (caretPos == src.length()) {
                sb.append(' ');
            }
        }

        try {
            int freeSz = textbox.getMaxSize() - textbox.size();
            if (freeSz < sb.length()) {
                sb.delete(freeSz, sb.length());
            }
        } catch (Exception e) {
        }
        setText(src + sb.toString());
    }
    public int getCaretPos() {
        int pos = textbox.getCaretPosition();
        if (pos < 0)
            pos = textbox.getString().length();
        return pos;
    }
    public final void setText(String body) {
        if (body != null) {
            if (body.length() > maxSize)
                body = body.substring(0, maxSize - 1);
            textbox.setString(body);
        }
    }
    private void setInitialCaps(boolean state) {
        textbox.setConstraints(state?TextField.INITIAL_CAPS_SENTENCE:TextField.ANY);
    }
    
    public void commandState() {
//#ifdef CLIPBOARD
        textbox.addCommand(cmdCopy);
        if (!ClipBoardIO.getInstance().isEmpty()) {
            textbox.addCommand(cmdCopyPlus);
            textbox.addCommand(cmdPasteText);
        }
        //#endif
//#ifdef ARCHIVE
        textbox.addCommand(cmdArchive);
//#endif
//#if TEMPLATES
        textbox.addCommand(cmdTemplate);
//#endif        
    }
    
    public boolean executeCommand(Command c, Displayable displayable) { 
        
        body = textbox.getString();
        caretPos = getCaretPos();
        
        if (body.length() == 0)
            body = null;
        
//#ifdef ARCHIVE
	if (c==cmdArchive) { new ArchiveList(caretPos, 1, textbox); return true; }
//#endif
//#ifdef CLIPBOARD
        if (c == cmdCopy) {
            try {
                ClipBoardIO.getInstance().setClipBoard(body);
                if (!ClipBoardIO.getInstance().isEmpty()) {
                    textbox.addCommand(cmdCopyPlus);
                }
            } catch (Exception e) {/*no messages*/}
            return true;
        }
        if (c==cmdCopyPlus) {
            try {
                ClipBoardIO.getInstance().append(body);
            } catch (Exception e) {/*no messages*/}
            return true;
        }
        if (c==cmdPasteText) {
            insert(ClipBoardIO.getInstance().getClipBoard(), getCaretPos(), writespaces);
            return true; }
//#endif
//#if TEMPLATES
        if (c==cmdTemplate) { new ArchiveList(caretPos, 2, textbox); return true; }
//#endif
        return false;
    }
}
