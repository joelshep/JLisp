package org.ulithi.console;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * A custom block caret for the {@link Console}.
 */
public class BlockCaret extends DefaultCaret {
    /**
     * Default cursor blink rate.
     */
    private static final int DEFAULT_BLINK_RATE_MILLIS = 500;

    /**
     * Constructs a new {@link BlockCaret} instance.
     */
    public BlockCaret () {
        setBlinkRate(DEFAULT_BLINK_RATE_MILLIS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected synchronized void damage (final Rectangle r) {
        if (r == null) return;

        // Pass values to this caret's x, y, width and height (inherited from
        // java.awt.Rectangle).
        this.x = r.x;
        this.y = r.y;
        this.height = r.height;

        // A value for width was probably set by paint(), which we leave alone.
        // But the first call to damage() precedes the first call to paint(), so
        // in this case we must be prepared to set a valid width, or else
        // paint() will receive a bogus clip area and the caret will not get
        // drawn properly.
        if (width <= 0) {
            width = getComponent().getWidth();
        }

        // Repaint the caret, erasing the previous location.
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint (final Graphics g) {
        final JTextComponent comp = getComponent();

        if (comp == null) return;

        final int dot = getDot();
        Rectangle r;
        char dotChar;

        try {
            r = comp.modelToView(dot);
            if ( r == null ) return;
            dotChar = comp.getText(dot, 1).charAt(0);
        } catch (BadLocationException e) {
            return;
        }

        if (Character.isWhitespace(dotChar)) dotChar = '_';

        if ((x != r.x) || (y != r.y)) {
            // paint() has been called directly, without a previous call to
            // damage(), so do some cleanup. (This happens, for example, when
            // the text component is resized.)
            damage(r);
            return;
        }

        g.setColor(comp.getCaretColor());
        g.setXORMode(comp.getBackground());

        width = g.getFontMetrics().charWidth(dotChar);

        if (isVisible()) {
            g.fillRect(r.x, r.y, width, r.height);
        }
    }
}
