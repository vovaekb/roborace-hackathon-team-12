package cz.artin.hackathon.team12;

import cz.artin.hackathon.simulator.ProgressDashboard;

public class RunRace {

  public static void main(String... args) {
    new ProgressDashboard(3, 1, 4, 2, 3, 3, false).start();
  }

}
