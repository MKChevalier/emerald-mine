public class Score {

    World world;

    GameStatus gameStatus;
    int emeraldsRemaining;
    int emeraldsStolen;

    public Score(World world)
    {
        this.world = world;
    }

    public void Update()
    {
        gameStatus = world.getStatus();
        emeraldsRemaining = world.getEmeraldsRemaining();
        emeraldsStolen = world.getEmeraldsStolen();
    }
}
