public enum GameDifficulty {

    /**
     * URL=http://tutorials.jenkov.com/java/enums.html
     */

    HIGH  (6),  //calls constructor with value 3
    MEDIUM(10),  //calls constructor with value 2
    LOW   (15)   //calls constructor with value 1
    ; // semicolon needed when fields / methods follow


    private final int acceptableEmeraldLosses;

    GameDifficulty(int acceptableEmeraldLosses) {
        this.acceptableEmeraldLosses = acceptableEmeraldLosses;
    }

    public int getAcceptableEmeraldLosses() {
        return this.acceptableEmeraldLosses;
    }

}
