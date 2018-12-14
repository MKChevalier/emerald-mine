public class Score {

    World world;

    GameStatus gameStatus;
    int remainingEmeraldsInWorld;
    int remainingEmeraldsToWin;

    public Score(World world)
    {
        this.world = world;
    }

    public synchronized void Update()
    {
        gameStatus = world.getStatus();
        remainingEmeraldsInWorld = world.getRemainingEmeraldsInWorld();
        remainingEmeraldsToWin = world.getRemainingEmeraldsToWin();
    }
}
