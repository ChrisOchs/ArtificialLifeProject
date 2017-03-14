package artificiallife;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Chris
 */
public class WorldPanel extends JPanel {
    
    private World world;

    public WorldPanel() {

    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        BufferedImage buffer = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = buffer.createGraphics();

        g2d.setColor(new Color(70, 70, 0));
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        if(world == null) {
            g2d.drawString("WORLD UNINITIALIZED", this.getWidth() / 2, this.getHeight() / 2);
        } else {
            ArrayList<Food> foodEntities = world.getFood();

            g2d.setColor(Color.GREEN);


            for(int c = foodEntities.size() - 1; c >= 0; c--) {
                
                Food food = foodEntities.get(c);

                int size = (int)(Math.log10(food.getAmount()) / Math.log10(2));
                
                g2d.fillOval(food.getPosition().x.intValue(), food.getPosition().y.intValue(), size, size);
            }
            
            List<Agent> agents = world.getAgents();

            for(int c = agents.size() - 1; c >= 0; c--) {
                Agent a = agents.get(c);

                Vector2<Double> position = a.getPosition();
                int size = a.getSize();

                if(a instanceof Carnivore) {
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(Color.YELLOW);
                }

                g2d.fillOval(position.x.intValue() - size/2, position.y.intValue() - size/2, size, size);

                Vector2<Integer> dest = a.getDestination();

                if(dest != null) {
                    g2d.drawRect(dest.x, dest.y, 4, 4);
                }

                if(a.getGender().equals(Agent.Gender.Male)) {
                    g2d.setColor(Color.BLUE);
                } else {
                    g2d.setColor(Color.PINK);
                }
                
                g2d.fillRect(position.x.intValue() - size/2, position.y.intValue() - size/2, size, 4);

                if(a.isDead()) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("X", position.x.intValue(), position.y.intValue());
                }

                if(a.isPregnant()) {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString("P", position.x.intValue(), position.y.intValue());
                }
            }
        }

        g.drawImage(buffer, 0, 0, this);
    }
}
