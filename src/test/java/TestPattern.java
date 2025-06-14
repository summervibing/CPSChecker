import de.marvin.cps.click.pattern.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPattern {

    private Pattern pattern;

    @BeforeEach
    public void setUp() {
        this.pattern = new Pattern();

        // Simulating 40 ticks with clicks and attacks
        for (int i = 0; i < 5; i++) {
            this.pattern.nextTick(); // 5 ticks with no clicks or attacks
        }
        for (int i = 0; i < 9; i++) {
            this.pattern.nextTick();
            // Run through 9 ticks with 1 click per tick
            this.pattern.registerClick(false);
        }
        for (int i = 0; i < 6; i++) {
            this.pattern.nextTick(); // 6 ticks with no clicks or attacks
        }
        for (int i = 0; i < 15; i++) {
            this.pattern.nextTick();
            // Run through 15 ticks with 3 clicks/attacks per tick
            this.pattern.registerAttack();
            this.pattern.registerClick(false);
            this.pattern.registerClick(false);
            this.pattern.registerClick(false);
        }
        for (int i = 0; i < 5; i++) {
            this.pattern.nextTick(); // 5 ticks with no clicks or attacks
        }
    }

    @Test
    public void testHistoryPatternDisplay() {
        var display = this.pattern.history();
        assertEquals(
                "§r §r §r §r §r " +
                        "§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA" +
                        "§r §r §r §r §r §r " +
                        "§aC§aC§aC§aC§aC§aC§aC§aC§aC" +
                        "§r §r §r §r §r ",
                display,
                "History display does not match expected output."
        );
    }

    @Test
    public void testStreakPatternDisplay() {
        var display = this.pattern.streak();

        assertEquals(
                "§r §r §r §r §r " +
                        "§c15(§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§cA§c)" +
                        "§r §r §r §r §r §r " +
                        "§e9(§aC§aC§aC§aC§aC§aC§aC§aC§aC§e)" +
                        "§r §r §r §r §r ",
                display,
                "Streak display does not match expected output."
        );
    }

}
