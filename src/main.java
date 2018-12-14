import java.io.IOException;

public class main {

    /**
     * Main entry point.
     */
    public static void main(final String[] args)
    {
        init();
    }

    public static void  init()
    {
        final boolean READWORLDFROMFILE = false;

        World world;
        Score score;

        GUI gui;

        if (READWORLDFROMFILE) {

            // Create the EM board
            try {
                System.out.println("Working Directory = " +
                        System.getProperty("user.dir"));
                String fileName = "./WorldDefinitions/example2.txt";
                System.out.println("Testing file: " + fileName + "...");
                world = new World(fileName, GameDifficulty.MEDIUM);
                System.out.println("Successfully loaded world.");
                // catch possible exceptions from the creation of a world from a file
            } catch (BadFileFormatException e) {
                world = null;
                System.out.println(e);
            } catch (IOException e) {
                world = null;
                e.printStackTrace();
            }
        }
        else {
            world = new World(20, 20, 25, 5, 10, 200, 2, 2, 2, GameDifficulty.MEDIUM);
        }

        if (world!=null)
        {
            score = new Score(world);
            score.Update();

            gui = new GUI(world, score);
            gui.init();

            Thread sim = new Simulator(world, gui);
            sim.start();

        }

    }
}
