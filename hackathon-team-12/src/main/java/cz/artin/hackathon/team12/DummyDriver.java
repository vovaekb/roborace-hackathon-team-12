package cz.artin.hackathon.team12;

import com.badlogic.gdx.math.Vector2;
import cz.artin.hackathon.simulator.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.PI;

public class DummyDriver implements Driver {

  private final String name;
  private final CarColor color;
  private Way way;

  public DummyDriver(String name, CarColor color) {
    this.name = name;
    this.color = color;
  }

  @NotNull
  @Override
  public CarColor getColor() {
    return color;
  }

  @NotNull
  @Override
  public String getName() {
    return name;
  }

  @Override
  public void enterRace(@NotNull Way way) {
    this.way = way;
  }

  @NotNull
  @Override
  public Drive getDrive(@NotNull CarInfo self, @NotNull List<CarInfo> cars) {
    Vector2 position = self.getPosition();
    int segment = way.closestSegment(position);

    float dist = way.distanceFromSegment(position, segment);
    float p = dist * way.sideOfSegment(position, segment) / way.getWidth();

    float angle = way.angleFromSegment(self.getAngle(), segment) / PI * 180;

    return new Drive(1f, 2*p - angle/20, true);
  }

}
