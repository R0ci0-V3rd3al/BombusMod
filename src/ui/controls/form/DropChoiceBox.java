/*
 * ChoiceBox.java
 *
 * Created on 20.05.2008, 9:06
 *
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
 */
package ui.controls.form;

import Colors.ColorTheme;
import Fonts.FontCache;
import images.RosterIcons;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import ui.IconTextElement;

/**
 *
 * @author ad
 */
public class DropChoiceBox
        extends IconTextElement {
    
    public int index=0;
    
    public Vector items=new Vector();
    
    private boolean selectable=true;
    
    private int colorItem;
    private int colorBorder;
    private int colorBGnd;
    
    private String caption="";
    
    private Font font;
    private int fontHeight;
    
    private Font captionFont;
    private int captionFontHeight;

    private int itemHeight=0;
    
    /**
     * Creates a new instance of ChoiceBox
     */
    public DropChoiceBox(String caption) {
        super(RosterIcons.getInstance());
        this.caption=(caption==null)?"":caption;
        
        font=FontCache.getFont(false, FontCache.roster);
        fontHeight=font.getHeight();
        itemHeight=fontHeight;
        
        if (caption!=null) {
            captionFont=FontCache.getFont(true, FontCache.msg);
            captionFontHeight=captionFont.getHeight();
            itemHeight+=captionFontHeight;
        }
    }
    
    public int getCaptionLength() {
        if (caption==null) return 0;
        if (caption.equals("")) return 0;
        return captionFont.stringWidth(caption);
    }

    public int getTextLength() {
        String text=getTextValue();
        if (text.equals("")) return 0;
        return font.stringWidth(text);
    }

    private String getTextValue() {
        if (items.size()<1) return "";
        return (String) items.elementAt(index);
    }

    public String toString() {
        return (getCaptionLength()>getTextLength())?caption:getTextValue();
    }

    public void onSelect() {
        if (items.size()>1)
            new DropListBox(items, this);
        }

    public int getValue() { return index; }
    
    public void add(String value) { items.addElement(value); }
    
    public void setSelectedIndex(int index) { 
        if (index>items.size()-1)
            index=0;
        this.index=index;
    }
    
    public int size() { 
        return items.size();
    }
    
    public int getSelectedIndex() { return index; }
    
    public void drawItem(Graphics g, int ofs, boolean sel) {
        colorItem=ColorTheme.getColor(ColorTheme.CONTROL_ITEM);
        colorBorder=ColorTheme.getColor(ColorTheme.CURSOR_OUTLINE);
        colorBGnd=ColorTheme.getColor(ColorTheme.LIST_BGND);

        int width=g.getClipWidth();
        int height=fontHeight;

        int oldColor=g.getColor();
        
        int thisOfs=0;
        
        int y=0;
        if (caption!=null) {
            thisOfs=(getCaptionLength()>width)?-ofs:2;
            g.setFont(captionFont);
            FontCache.drawString(g,caption, thisOfs, y, Graphics.TOP|Graphics.LEFT);
            y=captionFontHeight;
        }

        g.setColor(colorBGnd);
        g.fillRect(0, y, width-1, height-1);

        g.setColor((sel)?colorBorder:colorItem);
        g.drawRect(0, y, width-1, height-1);

        g.setColor(oldColor);
        
        if (getTextLength()>0) {
            thisOfs=(getTextLength()>width)?-ofs+4:4;
            g.setFont(font);
            FontCache.drawString(g,getTextValue(), thisOfs, y, Graphics.TOP|Graphics.LEFT); 
        }
        
        if (size()>1)
            il.drawImage(g, RosterIcons.ICON_COLLAPSED_INDEX, (width-il.getHeight())-1, ((y)+height/2)-il.getHeight()/2);
    }
    
    public int getVHeight(){
        return itemHeight;
    }

    public boolean handleEvent(int keyCode) {
        if (items.size()<1) return false;
        
         switch (keyCode) {
             case 5:
                onSelect();
                return true;
         }
        return false;
    }
    
    public boolean isSelectable() { return selectable; }
    
    public void afterSelect() {};
}
