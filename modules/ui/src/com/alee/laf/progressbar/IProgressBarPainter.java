package com.alee.laf.progressbar;

import com.alee.painter.SpecificPainter;

import javax.swing.*;

/**
 * Base interface for JProgressBar component painters.
 *
 * @author Alexandr Zernov
 */

public interface IProgressBarPainter<E extends JProgressBar, U extends WebProgressBarUI> extends SpecificPainter<E, U>
{
}