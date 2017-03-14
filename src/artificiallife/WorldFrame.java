package artificiallife;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Chris
 */
public class WorldFrame extends JFrame {
    private World world;

    private WorldPanel worldPanel;

    public WorldFrame() {
        this.setTitle("CS670 Project");
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        worldPanel = new WorldPanel();

        worldPanel.setPreferredSize(new Dimension(1300, 800));

        Timer updateTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                world.update();
                worldPanel.repaint();
            }
        });

        JPanel layoutPanel = new JPanel(new BorderLayout());
        
        layoutPanel.add(new JScrollPane(worldPanel), BorderLayout.CENTER);

        this.add(layoutPanel);
        
        this.setVisible(true);

        world = new World(worldPanel.getWidth(), worldPanel.getHeight());
        worldPanel.setWorld(world);

        layoutPanel.add(new SimulationMenu(world), BorderLayout.WEST);

        layoutPanel.validate();

        updateTimer.start();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
