/**
 * Simulates the main game loop in real time.
 * @author cambolbro (based on code from previous years).
 */
class Simulator extends Thread 
{
	private final World world;
	private final GUI gui;
	private final int   frameDelay = 500;  // milliseconds

	public Simulator(final World world, final GUI gui)
	{
		this.world = world;
		this.gui = gui;
	}

	/**
	 * Simulates the world.
	 */
	public void run()
	{
		// int outcome = World.Playing;
		while (true) 
		{
			// sleep for some frame delay
			try 
			{
				Thread.sleep(frameDelay);
			}
			catch(Exception e) 
			{
				e.printStackTrace();
				System.exit(-1);
			}


			world.moveMonsters();
			gui.redraw();


			// TODO: get "Play Again" to work
/*			if (world.getStatus() != GameStatus.PLAYING) {

				ReplayDialog rpd = new ReplayDialog();

				if (rpd.AskReplayQuestion()==0){
					// Play again
					System.out.println("Play Again");
					gui.dispose();
					main.init();
				} else {
					// Quit game
					System.exit(-1);
				}
			}*/




/*			// Now change the world
			final boolean deathByMonster = world.moveMonsters();
			if (deathByMonster) 
			{ 
				outcome = World.Loss; 
				break; 
			}

			final boolean deathByGravity = world.applyGravity(-1, -1); 
			if (deathByGravity) 
			{
				outcome = World.Loss;
				break;
			}	*/

//			gui.redraw();  // redraw the world
		}
    
		// Add code: print message based on outcome
//		System.exit(-1);
	}
}