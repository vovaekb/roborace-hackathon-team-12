package cz.artin.hackathon.team12;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import cz.artin.hackathon.simulator.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Driver for testing purposes. Control car by using arrows and space, or set up your own keys.
 */
public class KeyboardDriver implements Driver {

  private final float turning;
  private final float acceleration;

  public KeyboardDriver() {
    this(1f, 1f);
  }

  public KeyboardDriver(float turning, float acceleration) {
    this.turning = turning;
    this.acceleration = acceleration;
  }

  @NotNull
  @Override
  public String getName() {
    return "Keyboard";
  }

  @NotNull
  @Override
  public CarColor getColor() {
    return CarColor.RED;
  }

  @Override
  public void enterRace(@NotNull Way way) {}

  @Override
  @NotNull
  public Drive getDrive(@NotNull CarInfo self, @NotNull List<CarInfo> Driver) {
    float accel = 0f;
    if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
      accel = acceleration;
    } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
      accel = -acceleration;
    }

    float turn = 0f;
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      turn = turning;
    } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      turn = -turning;
    }

    boolean shoot = Gdx.input.isKeyPressed(Input.Keys.SPACE);

    return new Drive(accel, turn, shoot);
  }
}
