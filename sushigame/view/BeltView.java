package sushigame.view;

import java.awt.Component;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import comp401.sushi.IngredientPortion;
import comp401.sushi.Plate;
import sushigame.model.Belt;
import sushigame.model.BeltEvent;
import sushigame.model.BeltObserver;
import sushigame.plate_imgs.*;

/* class contains view of the Sushi Belt on main frame and all components...
*  ...associated with the view of the Belt
*/
public class BeltView extends JPanel implements BeltObserver {

	//encapsulates Belt object passed to constructor
	private Belt belt;
	//stores JButtons, JFrames, and JLabels in individual array for easy access to whole class
	private JButton[] belt_buttons;
	private JFrame[] info_frames;
	private JLabel[] plate_info;
	private final Dimension screenSize, windowSize;
	private final int wdwLeft, wdwTop;

	/*	Initializes instance variables and registers observers
	 * 	sets layout of this JPanel
	 * 	sets every position of Belt initially to zero...
	 *  ...sets all JFrames to no plate info, and sets all JButtons to nothing
	 */
	public BeltView(Belt b) throws IOException {
		this.belt = b;
		belt.registerBeltObserver(this);
		setLayout(new CircleLayout(true));
		belt_buttons = new JButton[belt.getSize()];
		plate_info = new JLabel[belt.getSize()];
		info_frames = new JFrame[belt.getSize()];
		screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
		windowSize = new Dimension(getPreferredSize());
		wdwLeft = -650 + screenSize.width / 2 - windowSize.width / 2;
		wdwTop = -100 + screenSize.height / 2 - windowSize.height / 2;
		
		//creates all new components needed for each position on Belt
		for (int i = 0; i < belt.getSize(); i++) {
			JButton pbutton = new JButton("");
			info_frames[i] = new JFrame("");
			plate_info[i] = new JLabel("");
			plate_info[i].setText("There is no plate at position. This position on the Belt is: " + i);
			plate_info[i].setHorizontalAlignment(SwingConstants.CENTER);
			
			//dimensions are optimal size for fitting entire button on main_frame evenly
			pbutton.setMinimumSize(new Dimension(75, 50));
			pbutton.setPreferredSize(new Dimension(75, 50));
			pbutton.setOpaque(true);

			/* required for getting positions in info_frames array...
			 * ...because a int labeled final is required inside MouseListener
			 */
			final int x = i;
			pbutton.addMouseListener(new MouseAdapter() {
				
				/* shows the JFrame associated with pbutton if pbutton...
				 * ...is moused over 
				 */
	            @Override
	            public void mouseEntered(MouseEvent e) {
	            	info_frames[x].add(plate_info[x]);
	            	
	            	info_frames[x].setPreferredSize(new Dimension(400, 450));
	            	
	            	info_frames[x].pack();
	            	info_frames[x].setVisible(true);
	   		     	info_frames[x].setLocation(wdwLeft, wdwTop);
	            	
	            }

	            /* disposes (closes and essentially "un"-packs() entire JFrame)...
	             * ...if mouse leaves pbutton being hovered over
	             * Java still has all components, just need to re-pack() them and...
	             * set visible to see again
	             */
	            @Override
	            public void mouseExited(MouseEvent e) {
	            	
	            	info_frames[x].dispose();
	            	
	            }
	        });
			
			//pbutton is added as a Component to JPanel, then pbutton is added to belt_buttons array
			add(pbutton);
			belt_buttons[i] = pbutton;
		}
		//called to get updated version of Belt
		refresh();
	}

	//refresh() is called whenever any changes on Belt occur
	@Override
	public void handleBeltEvent(BeltEvent e) throws IOException {	
		refresh();
	}

	//updates the view of the Belt
	private void refresh() throws IOException {
		//updates all components at every position in Belt
		for (int i=0; i<belt.getSize(); i++) {
			Plate p = belt.getPlateAtPosition(i);
			JButton pbutton = belt_buttons[i];
			
			/* required for getting positions in info_frames array...
			 * ...because a int labeled final is required inside MouseListener
			 */
			final int x = i;

			/* if the plate at the given position is null (i.e. no plate at position)...
			 * ...then reset JFrame to say there is no plate at position, add to...
			 * ...info_frames array, and set the Icon previously associated with position to null
			 */
			if (p == null) {
				plate_info[x].setText("There is no plate at position.  This position on the Belt is: " + i);
				info_frames[x].add(plate_info[x], SwingConstants.CENTER);
				pbutton.setIcon(null);
				
			} else {
				
				//used for determining if the Sushi on plate is Nigiri, Sashimi, or Roll
				String type_determiner = p.getContents().getName();
				if(type_determiner.contains("sashimi") || type_determiner.contains("nigiri")){
					/* sets all info needed for Nigiri or Sashimi to specific label...
					 * ...at given position to be put on JFrame at position
					 * uses html for easier formatting
					 */
					plate_info[x].setText("<html> <b>Plate Info</b> <br> <br>Color: " + p.getColor() + "<br> <br> Type of Sushi: " 
							+ type_determiner + "<br> <br> Chef of Plate: Chef " + p.getChef().getName() 
							+ "<br> <br> Age of Plate: " + belt.getAgeOfPlateAtPosition(x) + "<br> <br> "
									+ "This position on the Belt is: " + x);
				//this case means it's roll
				}else{
					/*	gets info of all Ingredients in Roll in form of array
					 * 	used to determine what kinds of Ingredients and how much of each on Roll
					 */
					IngredientPortion[] roll_ingreds = p.getContents().getIngredients();
					/* sets all info needed for Roll to specific label...
					 * ...at given position to be put on JFrame at position
					 * uses html for easier formatting
					 */
					plate_info[x].setText("<html> <b>Plate Info</b> <br> <br> Color: " + p.getColor() + "<br> <br> Type of Sushi: " 
							+ type_determiner + "<br> <br> Ingredients in Roll: " + printRollIngreds(roll_ingreds) + 
							"<br> Amount of Ingredients: " + getIngredsAmount(roll_ingreds) + "<br> Chef of Plate: Chef " 
							+ p.getChef().getName() + "<br> <br> Age of Plate: " + belt.getAgeOfPlateAtPosition(x) 
							+ "<br> <br> This position on the Belt is: " + x);
				}
				pbutton.addMouseListener(new MouseAdapter() {

					
					/* shows the JFrame associated with pbutton if pbutton...
					 * ...is moused over 
					 */
		            @Override
		            public void mouseEntered(MouseEvent e) {
	
		            	info_frames[x].setPreferredSize(new Dimension(400, 450));
		            	
		            	info_frames[x].pack();
		            	info_frames[x].setVisible(true);
		   		     	info_frames[x].setLocation(wdwLeft, wdwTop);
		            	
		            }

		            /* disposes (closes and essentially "un"-packs() entire JFrame)...
		             * ...if mouse leaves pbutton being hovered over
		             * Java still has all components, just need to re-pack() them and...
		             * set visible to see again
		             */
		            @Override
		            public void mouseExited(MouseEvent e) {
		            	
		            	info_frames[x].dispose();
		            	
		            }
		        });
				//switches on the Color of the given Plate at position
				switch (p.getColor()) {
				case RED:
					//creates new RedPlate Icon, associates it with pbutton at location
					RedPlateImg red = new RedPlateImg();
					pbutton.setIcon(new ImageIcon(red.readImage()));
					break;
				case GREEN:
					//creates new GreenPlate Icon, associates it with pbutton at location
					GreenPlateImg green = new GreenPlateImg();
					pbutton.setIcon(new ImageIcon(green.readImage()));
					break;
				case BLUE:
					//creates new BluePlate Icon, associates it with pbutton at location
					BluePlateImg blue = new BluePlateImg();
					pbutton.setIcon(new ImageIcon(blue.readImage()));
					break;
				case GOLD:
					//creates new GoldPlate Icon, associates it with pbutton at location
					GoldPlateImg gold = new GoldPlateImg();
					pbutton.setIcon(new ImageIcon(gold.readImage())); 
					break;
				}
			}
		}
		
	}
	
	
	/*	called in case of setting JLabel in plate_info at given position...
	 * 	...if the Sushi type of PLate is a Roll to store all of the...
	 * 	...Ingredients in Roll in a String and return it
	 */
	private String printRollIngreds(IngredientPortion[] roll_ingreds){
		String ingreds_printed = "<html>";
		int count = 1;
		/*	prints 4 ingreds on one line, then goes to next line if...
		 * 	...there is still more Ingredients
		 */
		for(IngredientPortion ingreds : roll_ingreds){
			if(count < 5){
				//count used to determine where commas and line breaks should be placed
				if(count == roll_ingreds.length){
					ingreds_printed += ingreds.getName() + "<br>";
				}
				else if(count == 1){
					ingreds_printed += " " + ingreds.getName() + ", ";
				}
				else{
					ingreds_printed += ingreds.getName() + ", ";
				}
			}
			else{
				//breaks line if 4 ingreds have been stored already, then keeps printing on next line
				if(count == 5){
					ingreds_printed += "<br>";
				}
				if(count == roll_ingreds.length){
					ingreds_printed += ingreds.getName() + "<br>";
				}
				else{
					ingreds_printed += ingreds.getName() + ", ";
				}
			}
			count++;
		}
		
		return ingreds_printed;
	}
	
	/*	called in case of setting JLabel in plate_info at given position...
	 * 	...if the Sushi type of PLate is a Roll to store all of the...
	 * 	...Ingredient's amounts in Roll in a String
	 */
	private String getIngredsAmount(IngredientPortion[] roll_ingreds){
		//breaks to new line initially because each Ingredient Amount will be stored on new line
		String ingreds_amount = "<html> <br>";
		//gets amount of every Ingredient and stores it in a variable
		for(IngredientPortion ingreds : roll_ingreds){
			//rounds amount to two decimal places then stores it
			ingreds_amount += (((int) ((ingreds.getAmount() * 100.0)+0.5))/100.0) + " of " + ingreds.getName() + "<br>";
			
		}
		return ingreds_amount;
	}
}

/**
 * This layout manager allows you to place components to form a circle within a
 * Container 
 */

class CircleLayout implements LayoutManager {

  ArrayList components;

  ArrayList names;

  private boolean isCircle;

  /**
   * Creates a new CircleLayout that lays out components in a perfect circle
   */

  public CircleLayout() {
    this(true);
  }

  /*
   * Creates a new CircleLayout that lays out components in either an Ellipse or
   * a Circle. Ellipse Layout is not yet implemented.
   * 
   * @param circle
   *          Indicated the shape to use. It's true for circle or false for
   *          ellipse.
   */
  public CircleLayout(boolean circle) {
    isCircle = circle;
  }

  /**
   * For compatibility with LayoutManager interface
   */
  public void addLayoutComponent(String name, Component comp) {
  }

  /**
   * Arranges the parent's Component objects in either an Ellipse or a Circle.
   * Ellipse is not yet implemented.
   */
  public void layoutContainer(Container parent) {
    int x, y, w, h, s, c;
    int n = parent.getComponentCount();
    double parentWidth = parent.getSize().width;
    double parentHeight = parent.getSize().height;
    Insets insets = parent.getInsets();
    int centerX = (int) (parentWidth - (insets.left + insets.right)) / 2;
    int centerY = (int) (parentHeight - (insets.top + insets.bottom)) / 2;

    Component comp = null;
    Dimension compPS = null;
    if (n == 1) {
      comp = parent.getComponent(0);
      x = centerX;
      y = centerY;
      compPS = comp.getPreferredSize();
      w = compPS.width;
      h = compPS.height;
      comp.setBounds(x, y, w, h);
    } else {
      double r = (Math.min(parentWidth - (insets.left + insets.right), parentHeight
          - (insets.top + insets.bottom))) / 2;
      r *= 0.75; // Multiply by .75 to account for extreme right and bottom
                  // Components
      for (int i = 0; i < n; i++) {
        comp = parent.getComponent(i);
        compPS = comp.getPreferredSize();
        if (isCircle) {
          c = (int) (r * Math.cos(2 * i * Math.PI / n));
          s = (int) (r * Math.sin(2 * i * Math.PI / n));
        } else {
          c = (int) ((centerX * 0.75) * Math.cos(2 * i * Math.PI / n));
          s = (int) ((centerY * 0.75) * Math.sin(2 * i * Math.PI / n));
        }
        x = c + centerX;
        y = s + centerY;

        w = compPS.width;
        h = compPS.height;

        comp.setBounds(x, y, w, h);
      }
    }

  }

  /**
   * Returns this CircleLayout's preferred size based on its Container
   * 
   * @param target
   *          This CircleLayout's target container
   * @return The preferred size
   */

  public Dimension preferredLayoutSize(Container target) {
    return target.getSize();
  }

  /**
   * Returns this CircleLayout's minimum size based on its Container
   * 
   * @param target
   *          This CircleLayout's target container
   * @return The minimum size
   */
  public Dimension minimumLayoutSize(Container target) {
    return target.getSize();
  }

  /**
   * For compatibility with LayoutManager interface
   */
  public void removeLayoutComponent(Component comp) {
  }

  /**
   * Returns a String representation of this CircleLayout.
   * 
   * @return A String that represents this CircleLayout
   */
  public String toString() {
    return this.getClass().getName();
  }

}
