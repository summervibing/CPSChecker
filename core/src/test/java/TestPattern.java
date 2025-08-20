import de.marvin.cps.core.click.ClickSession;
import de.marvin.cps.core.click.ClickType;
import de.marvin.cps.core.pattern.PatternType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPattern {

    private ClickSession session;

    @BeforeEach
    public void setUp() {
        this.session = new ClickSession();

        // Simulating 40 ticks with clicks and attacks
        for (int i = 0; i < 5; i++) {
            this.session.nextTick(); // 5 ticks with no clicks or attacks
        }
        for (int i = 0; i < 9; i++) {
            this.session.nextTick();
            // Run through 9 ticks with 1 click per tick
            this.session.registerClick(ClickType.LEFT_CLICK);
        }
        for (int i = 0; i < 6; i++) {
            this.session.nextTick(); // 6 ticks with no clicks or attacks
        }
        for (int i = 0; i < 15; i++) {
            this.session.nextTick();
            // Run through 15 ticks with 3 clicks/attacks per tick
            this.session.registerClick(ClickType.ATTACK);
            this.session.registerClick(ClickType.LEFT_CLICK);
            this.session.registerClick(ClickType.LEFT_CLICK);
            this.session.registerClick(ClickType.LEFT_CLICK);
        }
        for (int i = 0; i < 5; i++) {
            this.session.nextTick(); // 5 ticks with no clicks or attacks
        }
    }

    @Test
    public void testHistoryPatternDisplay() {
        var display = this.session.printPattern(ClickType.LEFT_CLICK, PatternType.HISTORY);
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
        var display = this.session.printPattern(ClickType.LEFT_CLICK, PatternType.STREAK);

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
