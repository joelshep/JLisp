package org.ulithi.console;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple console window for a command-line application, implemented with Swing components.
 * <p>
 * A {@link Console} handles the standard I/O streams in a seemingly backwards way. It
 * <em>reads</em> from STDOUT and STDERR and <em>writes</em> to STDIN. If it is viewed as a sink
 * for an application's STDOUT and STDERR streams, and a source for its STDIN stream, its
 * behavior makes more sense.
 * <p>
 * Initializing the <code>Console</code> itself is simple:<pre>
 *    Console console = new Console();</pre>
 * This will create a new <code>Console</code> that will display whatever the application has
 * written to STDOUT and STDERR, and will pass keyboard input to STDIN.
 * <p>
 * Internally, a <code>Console</code> starts two daemon threads to read from STDOUT and STDERR.
 * <p>
 * Original located at <a href="http://www.comweb.nl/java/Console/Console.html">...</a>
 *
 * @author RJHM van den Bergh , rvdb@comweb.nl
 *
 * @history 02-07-2012 David MacDermot Marked: DWM 02-07-2012
 *           Added KeyListener to pipe text to STDIN.
 *           Added custom block style caret.
 *           Added various other customizations.
 *
 *          10-16-2012 Joel Shepherd
 *           Explicit imports.
 *           Moved STDOUT/STDERR reader into own class (TextAreaSink).
 *           Removed wait() from STDOUT/STDERR reader.
 */
public class Console extends WindowAdapter implements WindowListener, ActionListener {
    /**
     * Connects a {@link JTextArea} to a {@link PipedInputStream} so that character data from the
     * stream is rendered in the text area.
     * <p>
     * Intended to be run on its own thread.
     */
    private static final class TextAreaSink implements Runnable {
        /**
         * The JTextArea receiving character data.
         */
        private final JTextArea textArea;

        /**
         * The PipedInputStream providing character data to the text area.
         */
        private final PipedInputStream inputPipe;

        /**
         * A thread of execution for this sink.
         */
        private Thread thread;

        /**
         * If true, indicates that the execution thread should terminate.
         */
        private final AtomicBoolean quit = new AtomicBoolean(false);

        /**
         * Constructs a new {@link TextAreaSink}, connecting the given {@link JTextArea} to the
         * given {@link PipedInputStream}.
         *
         * @param textArea The <code>JTextArea</code> to receive character data.
         * @param inputPipe The <code>PipedInputStream</code> providing character data to the
         *                  text area.
         */
        public TextAreaSink (final JTextArea textArea, final PipedInputStream inputPipe) {
            this.textArea = textArea;
            this.inputPipe = inputPipe;
        }

        /**
         * Starts a daemon thread for this {@link TextAreaSink}.
         */
        public void start () {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        /**
         * Stops the daemon thread for this {@link TextAreaSink}.
         */
        public void stop () {
            quit.set(true);

            try {
                if ( thread != null ) thread.join(1000);
                inputPipe.close();
            } catch (final Exception e) {

            }
        }

        /**
         * Reads data from the {@link PipedInputStream} and renders it on the {@link JTextArea}.
         * <p>
         * {@inheritDoc}
         */
        @Override
        public void run () {
            try {
                while (true) {
                    final int b = inputPipe.read();

                    if ( b >= 0 ) {
                        final String input = read(b, inputPipe);
                        updateTextArea(input);
                    }

                    if ( b == -1 || quit.get() ) return;
                }
            }
            catch (Exception e) {
                updateTextArea("\nConsole encountered an internal error: " + e);
            }
        }

        /**
         * Reads character data from the given input stream and returns it as a <code>String</code>.
         * Data is read until the pipe is empty or the thread has been signaled to quit.
         *
         * @param firstByte The first byte of data read from the input stream.
         * @param in The input stream to read additional data from.
         * @return A <code>String</code> containing the character data read
         *         from the stream.
         * @throws IOException if an exception occurs while reading the stream.
         */
        private String read (final int firstByte, final InputStream in)
            throws IOException
        {
            int padding = 1;

            final StringBuilder builder = new StringBuilder();

            do {
                int available = in.available();

                if ( (available + padding) == 0 ) break;

                final byte[] b = new byte[available + padding];

                if ( padding > 0 ) {
                    b[0] = (byte)firstByte;
                }

                if ( b.length > padding ) {
                    in.read(b, padding, b.length - padding);
                }

                builder.append(new String(b, 0, b.length));

                padding = 0;
            }
            while ( !quit.get() );

            return builder.toString();
        }

        /**
         * Renders the given text in the {@link JTextArea}.
         * @param text The text to be rendered.
         */
        private void updateTextArea (final String text) {
            synchronized(textArea) {
                textArea.append(text);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }
    }

    /**
     * Enumerates the standard I/O streams used by the console.
     */
    private enum StandardStream {
        /**
         * The "standard" error output stream.
         */
        STDERR,

        /**
         * The "standard" input stream.
         */
        STDIN,

        /**
         * The "standard" output stream.
         */
        STDOUT
    }

    private final JFrame frame;
    private final JTextArea textArea;
    private final TextAreaSink stdoutSink;
    private final TextAreaSink stderrSink;

    private final PipedInputStream stdOutPipe = new PipedInputStream();
    private final PipedInputStream stdErrPipe = new PipedInputStream();
    private final PipedOutputStream stdInPipe = new PipedOutputStream();

    /**
     * Class Constructor
     */
    public Console () {
        // create all components and add them
        frame=new JFrame("");
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize=new Dimension((int)(screenSize.width/2),(int)(screenSize.height/2));
        int x=(int)(frameSize.width/2);
        int y=(int)(frameSize.height/2);
        frame.setBounds(x,y,frameSize.width,frameSize.height);

        textArea=new JTextArea();
        textArea.setCaret(new BlockCaret());
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
        textArea.setCaretColor(textArea.getForeground());
        this.setFont(new Font("Monospaced", Font.BOLD, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true);

        JButton button=new JButton("clear");

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(textArea),BorderLayout.CENTER);
        frame.getContentPane().add(button,BorderLayout.SOUTH);
        frame.setVisible(true);

        frame.addWindowListener(this);
        button.addActionListener(this);

        redirectOutputStream(StandardStream.STDOUT, this.stdOutPipe);
        redirectOutputStream(StandardStream.STDERR, this.stdErrPipe);
        redirectInputStream(this.stdInPipe);

        // Start two separate threads to read from the PipedInputStreams.
        stdoutSink = new TextAreaSink(textArea, stdOutPipe);
        stdoutSink.start();

        stderrSink = new TextAreaSink(textArea, stdErrPipe);
        stderrSink.start();

        textArea.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}

            public void keyTyped (KeyEvent e) {
                try {
                    final char c = e.getKeyChar();
                    stdInPipe.write(c);
                    if ( c == '\n' ) stdInPipe.flush();
                } catch (IOException ex) {

                }
            }
        });
    }

    private void redirectOutputStream (final StandardStream streamType,
                                       final PipedInputStream pipe)
    {
        try {
            final PipedOutputStream stream = new PipedOutputStream(pipe);

            switch (streamType) {
                case STDERR:
                    System.setErr(new PrintStream(stream, true));
                    break;

                case STDOUT:
                    System.setOut(new PrintStream(stream, true));
                    break;
            }
        }
        catch (IOException io) {
            textArea.append("Couldn't redirect " + streamType + " to this console:"
                          + io.getMessage());

            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    /**
     * Connects the given {@link PipedOutputStream} to the System input stream.
     * @param outPipe
     */
    private void redirectInputStream (final PipedOutputStream outPipe) {
        try
        {
            System.setIn(new PipedInputStream(outPipe));
        }
        catch (IOException io)
        {
            textArea.append("Couldn't redirect STDIN to this console: "
                          + io.getMessage());
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    /**
     * Stops this {@link Console Console's} readers and closes its input/output pipes.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public synchronized void windowClosed (final WindowEvent event) {
        // Stop all the threads!
        this.notifyAll();
        stdoutSink.stop();
        stderrSink.stop();

        try
        {
            stdInPipe.close();
        }
        catch (final Exception e)
        {}

        System.exit(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void windowClosing (final WindowEvent event) {
        frame.setVisible(false);
        frame.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void actionPerformed (final ActionEvent evt) {
        this.clear();
    }

    /**
     * Clear the console window.
     */
    public void clear () {
        textArea.setText("");
    }

    /**
     * @return The console's background color.
     */
    public Color getBackground () {
        return textArea.getBackground();
    }

    /**
     * @param bg the desired background Color
     */
    public void setBackground (final Color bg) {
        this.textArea.setBackground(bg);
    }

    /**
     * @return the console's foreground color
     */
    public Color getForeground () {
        return textArea.getForeground();
    }

    /**
     * @param fg the desired foreground Color
     */
    public void setForeground (final Color fg) {
        this.textArea.setForeground(fg);
        this.textArea.setCaretColor(fg);
    }

    /**
     * @return the console's font
     */
    public Font getFont () {
        return textArea.getFont();
    }

    /**
     * @param f the font to use as the current font
     */
    public void setFont (final Font f) {
        textArea.setFont(f);
    }

    /**
     * @param i the icon image to display in console window's corner
     */
    public void setIconImage (final Image i) {
        frame.setIconImage(i);
    }

    /**
     * @param title the console window's title
     */
    public void setTitle (final String title) {
        frame.setTitle(title);
    }
}
