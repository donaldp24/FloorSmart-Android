package com.tim.FloorSmart.PDF;

import android.graphics.Rect;
import com.pdfjet.Align;
import com.pdfjet.TextLine;

/**
 * Created with IntelliJ IDEA.
 * User: hanyong
 * Date: 6/21/14
 * Time: 2:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyTextLine extends TextLine {
    protected Rect _rt;
    protected int _align;

    public MyTextLine(com.pdfjet.Font font) {
        super(font);
    }

    public MyTextLine(com.pdfjet.Font font, String label) {
        super(font, label);
    }

    public void setRect(Rect rt, int align)
    {
        _rt = rt;

        if (align == Align.LEFT)
            super.setPosition(rt.left, rt.top);
        else if (align == Align.CENTER)
        {
            super.setPosition(rt.left + rt.width() / 2 -  super.getWidth() / 2, rt.top + super.getHeight() / 2);
        }
        else if (align == Align.RIGHT)
        {
            super.setPosition(_rt.right - super.getWidth(), _rt.top);
        }
    }

    public void setRect(float left, float top, float width, float height, int align)
    {
        Rect rt = new Rect((int)left, (int)top, (int)(left + width), (int)(top + height));
        _rt = rt;

        if (align == Align.LEFT)
            super.setPosition(rt.left, rt.top);
        else if (align == Align.CENTER)
        {
            super.setPosition(rt.left + rt.width() / 2 -  super.getWidth() / 2, rt.top + super.getHeight() / 2);
        }
        else if (align == Align.RIGHT)
        {
            super.setPosition(_rt.right - super.getWidth(), _rt.top);
        }
    }

    @Override
    public void setText(String str)
    {
        super.setText(str);

        if (_align == Align.CENTER)
            super.setPosition(_rt.left + _rt.width() / 2 -  super.getWidth() / 2, _rt.top);
        else if (_align == Align.RIGHT)
            super.setPosition(_rt.right - super.getWidth(), _rt.top);
    }


}
