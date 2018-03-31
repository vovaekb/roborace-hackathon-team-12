package cz.artin.hackathon.team12;

import cz.artin.hackathon.simulator.CarColor;
import cz.artin.hackathon.simulator.Driver;
import cz.artin.hackathon.simulator.OfficialTeam;
import cz.artin.hackathon.simulator.Team;
import cz.artin.hackathon.team12.aidriver.AiDriver;
import cz.artin.hackathon.team12.aidriver.DataCollectingDriver;
import cz.artin.hackathon.team12.aidriver.Driver_team12;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@OfficialTeam("team12")
public class Team12 implements Team {

  @NotNull
  @Override
  public String getName() {
    return "Slow&Calm";
  }

  @NotNull
  @Override
  public CarColor getColor() {
    return CarColor.BLUE;
  }

  @NotNull
  @Override
  public List<Driver> createDrivers(int count) {
    CarColor[] colors = CarColor.values();

    List<Driver> drivers = new ArrayList<>(count);

//    drivers.add(new KeyboardDriver());
//    drivers.add(new DataCollectingDriver());
//    drivers.add(new AiDriver());
    drivers.add(new Driver_team12());

    for (int i = drivers.size(); i < count; i++) {
      drivers.add(new DummyDriver("Dummy " + i, colors[i% colors.length]));
    }

    return drivers;
  }

}
