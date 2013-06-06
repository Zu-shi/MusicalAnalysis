package com.badlogic.audio.visualization;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComponent;

public class Ring extends JComponent implements Observer{

	java.awt.Color color = java.awt.Color.white; // The color of the ball
	java.awt.Point location = new java.awt.Point(150, 150); // the location of the
															// center of the
															// ball
	int counter = 0;
	double radius, radius2;
	boolean draw = true;
	
	public Ring(){
		setVisible(true);
		setBounds(new Rectangle(0, 0, 10000, 10000));
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		//System.out.print("o");
		counter++;
		radius += Math.pow((30 - counter)/7, 2);
		radius2 = radius - (20 - counter + 4);
        if(counter>20){counter=0;radius=0;draw=false;}
	}
	
	public void paintComponent(Graphics g) {
		if(draw){
			super.paintComponent(g); // paint whatever normally gets painted.
			//System.out.print("a");
				Graphics2D g2 = (Graphics2D)g;
				Ellipse2D c1 = new Ellipse2D.Double(location.x - radius, location.y - radius, radius * 2, radius * 2);
				Ellipse2D c2 = new Ellipse2D.Double(location.x - radius2, location.y - radius2, radius2 * 2, radius2 * 2);
		        Area circle = new Area(c1);
		        circle.subtract(new Area(c2));
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        //g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		        //g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		        //g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2.setColor(color); // set the color to paint with
		        g2.fill(circle);
		}
	}
	
}
