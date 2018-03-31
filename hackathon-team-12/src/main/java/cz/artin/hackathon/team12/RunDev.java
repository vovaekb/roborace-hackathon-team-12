package cz.artin.hackathon.team12;

import com.google.common.collect.Lists;
import cz.artin.hackathon.simulator.DevelopmentGame;
import cz.artin.hackathon.simulator.Race;
import cz.artin.hackathon.simulator.Tracks;

public class RunDev {

  public static void main(String... args) {
    DevelopmentGame game = new DevelopmentGame(
      new Race(
        Tracks.daytona(),
        3,
        Lists.newArrayList(
          new Team12()
        ),
        2
      )
    );
    game.start();
  }

}
