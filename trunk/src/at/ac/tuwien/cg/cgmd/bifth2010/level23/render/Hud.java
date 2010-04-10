package at.ac.tuwien.cg.cgmd.bifth2010.level23.render;

import java.util.TimerTask;

import at.ac.tuwien.cg.cgmd.bifth2010.level17.math.Vector2;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.entities.Button;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.util.Settings;
import at.ac.tuwien.cg.cgmd.bifth2010.level23.util.TimeUtil;

/**
 * The Class Hud provides the HUD for activating boni and movement arrows
 *
 * @author Markus Ernst
 * @author Florian Felberbauer
 */
public class Hud 
{
	
	/** The gold bar button */
	private Button goldButton;
	
	/** The money button. */
	private Button moneyButton;
	
	/** The class handling time utilities */
	private TimeUtil timeUtil; 
	
	/**
	 * Th Class BurnTimer handles all tasks needed for the money bonus 
	 */
	private class BurnTimer extends TimerTask
	{

		/* (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		/**
		 * sets the balloon speed to the old value after the boost time ran up
		 */
		@Override
		public void run() 
		{
			Settings.BALLOON_SPEED -= Settings.BURN_BOOST;
			moneyButton.setActive(true);
			timeUtil.cancelTimer();
		}
		
	}
	
	/**
	 * Instantiates a new hud, including the buttons and the timer for boost operation.
	 */
	public Hud()
	{
		float topBounds = RenderView.getInstance().getTopBounds();
		float rightBounds = RenderView.getInstance().getRightBounds();
		goldButton = new Button(10, 10, new Vector2(0,topBounds-10));
		moneyButton = new Button(10, 10, new Vector2(rightBounds-10,topBounds-10));
		timeUtil = TimeUtil.getInstance();
	}
	
	/**
	 * Checks if the money button is active
	 * @return true if active, else otherwise
	 */
	public boolean isMoneyButtonActive() {
		return moneyButton.isActive();
	}
	
	/**
	 * Sets the active state of the money button 
	 * @param b true if button should be active, false otherwise
	 */
	public void setMoneyButtonActive(boolean b) {
		moneyButton.setActive(b); 
	}
	/**
	 * Tests if pressed
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return true, if pressed, false otherwise
	 */
	public boolean testPressed(float x, float y)
	{
		if(goldButton.isPressed(x, y))
		{
			Settings.BALLOON_SPEED += Settings.GOLD_BOOST;
			return true;
		}
			
		if(moneyButton.isPressed(x, y) && moneyButton.isActive())
		{
			moneyButton.setActive(false);
			Settings.BALLOON_SPEED += Settings.BURN_BOOST;
			timeUtil.scheduleTimer(new BurnTimer(), Settings.BURN_BOOST_TIME);
			return true;
		}
		
		return false;			
	}
	
	/**
	 * Renders the HUD 
	 */
	public void render()
	{
		goldButton.render();
		moneyButton.render();
	}
}
