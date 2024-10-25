import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.text.JTextComponent;

import javax.swing.Action;

public class Viewer {

    private static ResourceBundle resources;

    static {
        try {
            resources = ResourceBundle.getBundle("resources.Stylepad_MVC_Pattern");
        } catch (MissingResourceException mre) {
            System.err.println("Stylepad.properties not found");
            System.exit(0);
        }
    }

    private JTextComponent editor;
    private JMenuBar menubar;
    private Hashtable menuItems;
    private Hashtable commands;
    /**
     * Suffix applied to the key used in resource file
     * lookups for an image.
     */
    public static final String imageSuffix = "Image";

    /**
     * Suffix applied to the key used in resource file
     * lookups for a label.
     */
    public static final String labelSuffix = "Label";

    /**
     * Suffix applied to the key used in resource file
     * lookups for an action.
     */
    public static final String actionSuffix = "Action";

    public Viewer() {
        Controller controller = new Controller(this);

        // create the embedded JTextComponent
        editor = createEditor();
        // Add this as a listener for undoable edits.
        // editor.getDocument().addUndoableEditListener(undoHandler);

        // install the command table
        commands = new Hashtable();
        menuItems = new Hashtable();

        JScrollPane scroller = new JScrollPane();
        JViewport port = scroller.getViewport();
        port.add(editor);

        JFrame frame = new JFrame();
        frame.setTitle(resources.getString("Title"));
        frame.setBackground(Color.lightGray);
        frame.getContentPane().setLayout(new BorderLayout());

        // Stylepad stylepad = new Stylepad();
        frame.getContentPane().add("Center", scroller);
        frame.setJMenuBar(createMenubar());
        // frame.addWindowListener(new AppCloser());
        // frame.pack();
        frame.setLocation(400, 200);
        frame.setSize(500, 400);
        frame.setVisible(true);
    }

    /**
     * Create an editor to represent the given document.
     */
    protected JTextComponent createEditor() {
        JTextComponent c = new JTextArea();
        c.setDragEnabled(true);
        c.setFont(new Font("Arial", Font.PLAIN, 16));
        return c;
    }

    /**
     * Fetch the editor contained in this panel
     */
    protected JTextComponent getEditor() {
        return editor;
    }

    /**
     * Create the menubar for the app. By default this pulls the
     * definition of the menu from the associated resource file.
     */
    protected JMenuBar createMenubar() {
        JMenuItem mi;
        JMenuBar mb = new JMenuBar();

        String[] menuKeys = tokenize(getResourceString("menubar"));
        for (int i = 0; i < menuKeys.length; i++) {
            JMenu m = createMenu(menuKeys[i]);
            if (m != null) {
                mb.add(m);
            }
        }
        this.menubar = mb;
        return mb;
    }

    /**
     * Create a menu for the app. By default this pulls the
     * definition of the menu from the associated resource file.
     */
    protected JMenu createMenu(String key) {
        String[] itemKeys = tokenize(getResourceString(key));
        JMenu menu = new JMenu(getResourceString(key + "Label"));
        for (int i = 0; i < itemKeys.length; i++) {
            if (itemKeys[i].equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem mi = createMenuItem(itemKeys[i]);
                menu.add(mi);
            }
        }
        return menu;
    }

    /**
     * Take the given string and chop it up into a series
     * of strings on whitespace boundaries. This is useful
     * for trying to get an array of strings out of the
     * resource file.
     */
    protected String[] tokenize(String input) {
        Vector v = new Vector();
        StringTokenizer t = new StringTokenizer(input);
        String cmd[];

        while (t.hasMoreTokens())
            v.addElement(t.nextToken());
        cmd = new String[v.size()];
        for (int i = 0; i < cmd.length; i++)
            cmd[i] = (String) v.elementAt(i);

        return cmd;
    }

    /**
     * This is the hook through which all menu items are
     * created. It registers the result with the menuitem
     * hashtable so that it can be fetched with getMenuItem().
     * 
     * @see #getMenuItem
     */
    protected JMenuItem createMenuItem(String cmd) {
        JMenuItem mi = new JMenuItem(getResourceString(cmd + labelSuffix));
        URL url = getResource(cmd + imageSuffix);
        if (url != null) {
            mi.setHorizontalTextPosition(JButton.RIGHT);
            mi.setIcon(new ImageIcon(url));
        }
        String astr = getResourceString(cmd + actionSuffix);
        if (astr == null) {
            astr = cmd;
        }
        mi.setActionCommand(astr);
        Action a = getAction(astr);
        if (a != null) {
            mi.addActionListener(a);
            // a.addPropertyChangeListener(createActionChangeListener(mi));
            mi.setEnabled(a.isEnabled());
        } else {
            mi.setEnabled(false);
        }
        menuItems.put(cmd, mi);
        return mi;
    }

    protected String getResourceString(String nm) {
        System.out.println(nm);
        String str;
        try {
            str = resources.getString(nm);
        } catch (MissingResourceException mre) {
            str = null;
        }
        return str;
    }

    protected URL getResource(String key) {
        String name = getResourceString(key);
        if (name != null) {
            URL url = this.getClass().getResource(name);
            return url;
        }
        return null;
    }

    protected Action getAction(String cmd) {
        return (Action) commands.get(cmd);
    }

}