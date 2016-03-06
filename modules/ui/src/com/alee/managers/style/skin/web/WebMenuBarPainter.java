package com.alee.managers.style.skin.web;

import com.alee.extended.layout.ToolbarLayout;
import com.alee.laf.menu.IMenuBarPainter;
import com.alee.laf.menu.WebMenuBarUI;
import com.alee.managers.style.skin.web.data.decoration.IDecoration;

import javax.swing.*;

/**
 * @author Alexandr Zernov
 */

public class WebMenuBarPainter<E extends JMenuBar, U extends WebMenuBarUI, D extends IDecoration<E, D>>
        extends AbstractDecorationPainter<E, U, D> implements IMenuBarPainter<E, U>
{
    @Override
    public void install ( final E c, final U ui )
    {
        super.install ( c, ui );

        // Installing custom layout for the menu bar
        component.setLayout ( new ToolbarLayout ( 0 ) );
    }
}